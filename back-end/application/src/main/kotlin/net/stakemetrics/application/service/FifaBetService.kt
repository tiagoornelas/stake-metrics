package net.stakemetrics.application.service

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.Message
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.dtos.MessengerDTO
import net.stakemetrics.application.entities.dtos.toResponse
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.exceptions.EntityDoesntBelongToUserException
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.repositories.IFifaMatchRepository
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.application.workers.FifaBetClosingWorker
import net.stakemetrics.application.workers.FifaBetMessageBuilder
import net.stakemetrics.application.workers.tipsters.FifaTipsterHelper
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class FifaBetService(
    private val logger: Logger,
    private val userService: UserService,
    private val queueService: IQueueService,
    private val messageService: MessageService,
    private val messengerService: IMessengerService,
    private val fifaTipsterHelper: FifaTipsterHelper,
    private val fifaBetRepository: IFifaBetRepository,
    private val fifaStrategyService: FifaStrategyService,
    private val fifaMatchRepository: IFifaMatchRepository,
    private val fifaBetClosingWorker: FifaBetClosingWorker,
    private val fifaBetMessageBuilder: FifaBetMessageBuilder,
) {

    fun bet(payload: FifaBetDTO.BetRequest) {
        val fifaMatch = fifaMatchRepository.findById(payload.fifaMatchId)
            ?: throw InternalError("Match not found when trying to bet on it")

        val alreadyBet = fifaBetRepository.existsByStrategyAndMatch(payload.strategy, fifaMatch)

        if (alreadyBet) {
            logger.log("Already bet on the match ${fifaMatch.integrationId} with the strategy ${payload.strategy.id}")
            return
        }

        val handicap = if (isGoalLineMarket(payload.candidate)) payload.oddSnapshot.goalsHandicap else null

        val fifaBet = FifaBet(
            isPaperBet = payload.strategy.isPaperBetting,
            strategy = payload.strategy,
            match = fifaMatch,
            line = payload.candidate,
            handicap = handicap,
            odds = fifaTipsterHelper.getOddForCandidate(payload.candidate, payload.oddSnapshot),
            betTime = Date(),
            oddSnapshotId = payload.oddSnapshot.id,
        )

        if (!fifaBet.isPaperBet) sendBetMessagesToUserChats(fifaBet)
        fifaBetRepository.save(fifaBet)
    }

    private fun isGoalLineMarket(candidate: FifaMarketBetCandidates): Boolean {
        return FifaMarketTypes.GOAL_LINE.subTypes.any { subtype ->
            subtype.betCandidates.contains(candidate)
        }
    }

    fun sendBetMessagesToUserChats(fifaBet: FifaBet) {
        val activeUserChats = messengerService.listActiveUserChats(fifaBet.strategy?.user!!)
        val integratedActiveUserChats = activeUserChats.filter { it.chatId != null }
        val betMessage = fifaBetMessageBuilder.build(fifaBet)
        val successfulChats = mutableSetOf<MessengerChat>()

        integratedActiveUserChats.forEach { chat ->
            val message = Message(messengerChat = chat, text = betMessage)
            val messageSent = messengerService.sendToQueue(chat, betMessage, message.id)
            if (messageSent) {
                successfulChats.add(chat)
                messageService.save(message)
                fifaBet.messages.add(message)
            }
        }
    }

    fun closeBet(bet: FifaBet) {
        fifaBetClosingWorker.close(bet)
    }

    fun listBets(userEmail: String, page: Int, size: Int, showPaperBets: Boolean): Page<FifaBetDTO.BetResponse> {
        val user = userService.findByEmail(userEmail)
        val strategyIds = fifaStrategyService.findAllByUserId(user.id).map { it.id }
        return fifaBetRepository.listAllByStrategyIds(strategyIds, page, size, showPaperBets).map { it.toResponse() }
    }

    fun delete(userEmail: String, betId: UUID) {
        val user = userService.findByEmail(userEmail)
        val bet = fifaBetRepository.findById(betId)
        assertBetBelongsToUser(user, bet)
        fifaBetRepository.delete(bet)
        discardBetMessages(bet)
    }

    private fun assertBetBelongsToUser(user: User, bet: FifaBet) {
        if (bet.strategy?.user?.id != user.id) throw EntityDoesntBelongToUserException()
    }

    private fun discardBetMessages(bet: FifaBet) {
        bet.messages.forEach { message ->
            queueService.enqueueEditMessageTask(
                MessengerDTO.EditMessageEnqueueRequest(
                    messengerChat = message.messengerChat!!,
                    integrationMessageId = message.integrationMessageId!!,
                    newText = fifaBetMessageBuilder.buildDiscard(bet)
                )
            )
        }
    }
}