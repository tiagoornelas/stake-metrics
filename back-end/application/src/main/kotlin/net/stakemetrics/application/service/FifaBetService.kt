package net.stakemetrics.application.service

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.Message
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.dtos.toResponse
import net.stakemetrics.application.entities.exceptions.EntityDoesntBelongToUserException
import net.stakemetrics.application.repositories.IFifaBetRepository
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
    private val messageService: MessageService,
    private val fifaMatchService: FifaMatchService,
    private val messengerService: IMessengerService,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaPlayerService: FifaPlayerService,
    private val fifaTipsterHelper: FifaTipsterHelper,
    private val fifaBetRepository: IFifaBetRepository,
    private val fifaStrategyService: FifaStrategyService,
    private val fifaBetMessageBuilder: FifaBetMessageBuilder,
    private val fifaBetClosingWorker: FifaBetClosingWorker,
) {

    fun bet(payload: FifaBetDTO.BetRequest) {
        val alreadyBet =
            fifaBetRepository.existsByStrategyAndMatchIntegrationId(payload.strategy, payload.matchIntegrationId)

        if (alreadyBet) {
            logger.log("Already bet on the match ${payload.matchIntegrationId} with the strategy ${payload.strategy.id}")
            return
        }

        val fifaLeague = fifaLeagueService.findByIntegrationId(payload.leagueIntegrationId)
        val home = fifaPlayerService.findByName(payload.homePlayerName)
        val away = fifaPlayerService.findByName(payload.awayPlayerName)

        val fifaMatch = fifaMatchService.findByIntegrationId(payload.matchIntegrationId) ?: run {
            val newFifaMatch = FifaMatch(
                integrationId = payload.matchIntegrationId,
                time = payload.matchTime,
                league = fifaLeague,
                home = home,
                away = away
            )
            try {
                fifaMatchService.save(newFifaMatch)
                newFifaMatch
            } catch (e: Exception) {
                logger.logError(e)
                fifaMatchService.findByIntegrationId(payload.matchIntegrationId)
            }
        }

        val fifaBet = FifaBet(
            isPaperBet = payload.strategy.isPaperBetting,
            strategy = payload.strategy,
            match = fifaMatch,
            line = payload.candidate,
            handicap = payload.lineOdds.handicap,
            odds = fifaTipsterHelper.getOddForCandidate(payload.candidate, payload.lineOdds),
            oddOfferTime = payload.lineOdds.oddOfferTime,
            betTime = Date()
        )

        if (!fifaBet.isPaperBet) sendBetMessagesToUserChats(fifaBet)
        fifaBetRepository.save(fifaBet)
    }

    fun sendBetMessagesToUserChats(fifaBet: FifaBet) {
        val activeUserChats = messengerService.listActiveUserChats(fifaBet.strategy?.user!!)
        val betMessage = fifaBetMessageBuilder.build(fifaBet)
        val successfulChats = mutableSetOf<MessengerChat>()

        activeUserChats.forEach { chat ->
            val messageSent = messengerService.sendToQueue(chat, betMessage)
            if (messageSent) {
                successfulChats.add(chat)
                val message = Message(
                    messengerChat = chat,
                    text = betMessage
                )
                messageService.save(message)
                fifaBet.messages.add(message)
            }
        }
    }

    fun closeBet(bet: FifaBet) {
        fifaBetClosingWorker.close(bet)
    }

    fun listBets(userEmail: String, page: Int, size: Int): Page<FifaBetDTO.BetResponse> {
        val user = userService.findByEmail(userEmail)
        val strategyIds = fifaStrategyService.findAllByUserId(user.id).map { it.id }
        return fifaBetRepository.listAllByStrategyIds(strategyIds, page, size).map { it.toResponse() }
    }

    fun delete(userEmail: String, betId: UUID) {
        val user = userService.findByEmail(userEmail)
        val bet = fifaBetRepository.findById(betId)
        assertBetBelongsToUser(user, bet)
        fifaBetRepository.delete(bet)
    }

    private fun assertBetBelongsToUser(user: User, bet: FifaBet) {
        if (bet.strategy?.user?.id != user.id) throw EntityDoesntBelongToUserException()
    }
}