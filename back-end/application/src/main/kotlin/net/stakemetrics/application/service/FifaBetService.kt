package net.stakemetrics.application.service

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.dtos.MessengerDTO
import net.stakemetrics.application.entities.dtos.toResponse
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.exceptions.EntityDoesntBelongToUserException
import net.stakemetrics.application.entities.exceptions.FifaBetOnStartedMatchException
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.repositories.IFifaMatchRepository
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.application.workers.FifaActiveBetWorker
import net.stakemetrics.application.workers.FifaBetClosingWorker
import net.stakemetrics.application.workers.FifaBetMessageBuilder
import net.stakemetrics.application.workers.tipsters.FifaTipsterHelper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

@Service
class FifaBetService(
    private val logger: Logger,
    private val userService: UserService,
    private val queueService: IQueueService,
    private val fifaTipsterHelper: FifaTipsterHelper,
    private val fifaBetRepository: IFifaBetRepository,
    private val fifaStrategyService: FifaStrategyService,
    private val fifaActiveBetWorker: FifaActiveBetWorker,
    private val fifaMatchRepository: IFifaMatchRepository,
    private val fifaBetClosingWorker: FifaBetClosingWorker,
    private val fifaBetMessageBuilder: FifaBetMessageBuilder,
) {

    fun bet(payload: FifaBetDTO.BetRequest) {
        val fifaMatch = fifaMatchRepository.findById(payload.fifaMatchId)
            ?: throw InternalError("Match not found when trying to bet on it")

        val matchAlreadyStarted = hasMatchAlreadyBegun(fifaMatch)
        val alreadyBet = fifaBetRepository.existsByStrategyAndMatch(payload.strategy, fifaMatch)

        if (matchAlreadyStarted) throw FifaBetOnStartedMatchException(fifaMatch)

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

        if (!fifaBet.isPaperBet) fifaActiveBetWorker.workOnBet(fifaBet)
        fifaBetRepository.save(fifaBet)
    }

    private fun hasMatchAlreadyBegun(fifaMatch: FifaMatch): Boolean {
        return fifaMatch.time < Date()
    }

    private fun isGoalLineMarket(candidate: FifaMarketBetCandidates): Boolean {
        return FifaMarketTypes.ASIAN_GOAL_LINE.subTypes.any { subtype ->
            subtype.betCandidates.contains(candidate)
        }
    }

    fun closeBet(bet: FifaBet) {
        fifaBetClosingWorker.close(bet)
    }

    fun listBets(
        userEmail: String, strategyId: UUID, betFilter: FifaBetDTO.BetFilter
    ): Page<FifaBetDTO.BetResponse> {
        val strategy = fifaStrategyService.findById(strategyId)
        assureStrategyBelongsToUser(strategy, userEmail)
        val pageable = PageRequest.of(betFilter.page, betFilter.size)
        return fifaBetRepository.findBetsByStrategyAndFilter(userEmail, strategyId, betFilter, pageable)
            .map { it.toResponse() }
    }

    private fun assureStrategyBelongsToUser(strategy: FifaStrategy, userEmail: String) {
        if (strategy.user?.email != userEmail) {
            throw EntityDoesntBelongToUserException()
        }
    }

    fun delete(userEmail: String, betId: UUID) {
        val bet = fifaBetRepository.findById(betId)
        val user = userService.findByEmail(userEmail)
        assertBetBelongsToUser(user, bet)
        fifaBetRepository.delete(bet)
        discardBetMessages(bet)
    }

    fun discardHangingBet(bet: FifaBet) {
        if (!bet.isHanging()) {
            logger.log("Attempted to discard bet ${bet.id} but it is not hanging")
            return
        }

        fifaBetRepository.delete(bet)
        discardBetMessages(bet)
    }

    fun getStatistics(userEmail: String): FifaBetDTO.StatisticsResponse {
        val user = userService.findByEmail(userEmail)
        val openBets = fifaBetRepository.countOpenBetsByUser(user.id)

        val tz = user.timezoneOffset.id
        val zoneId = java.time.ZoneId.of(tz)
        val now = LocalDateTime.now(zoneId)

        val startOfTheDay = now.withHour(0).withMinute(0).withSecond(0).withNano(0)
        val startOfTheMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)

        val dayStatistics = fifaBetRepository.getMainStatisticsByUserAndDateBetween(user.id, tz, startOfTheDay, now)
        val monthStatistics = fifaBetRepository.getMainStatisticsByUserAndDateBetween(user.id, tz, startOfTheMonth, now)

        val dailyStartDate = now.minusDays(12).toLocalDate()
        val monthlyStartDate = now.minusMonths(12).toLocalDate()
        val endDate = now.toLocalDate()

        val dailyProfits = fifaBetRepository.getDailyProfits(
            user.id, tz, dailyStartDate, endDate
        ).map { FifaBetDTO.DailyProfit(it.date, it.profit) }

        val monthlyProfits = fifaBetRepository.getMonthlyProfits(
            user.id, tz, monthlyStartDate, endDate
        ).map { FifaBetDTO.MonthlyProfit(it.startDate, it.endDate, it.profit) }

        val possibleProfitOnOpenBets = fifaBetRepository.getPossibleProfitFromOpenBets(user.id)

        return FifaBetDTO.StatisticsResponse(
            openBets = openBets,
            possibleProfitOnOpenBets = possibleProfitOnOpenBets,
            dayStatistics = dayStatistics,
            monthStatistics = monthStatistics,
            last12DaysProfit = dailyProfits,
            last12MonthsProfit = monthlyProfits
        )
    }

    private fun assertBetBelongsToUser(user: User, bet: FifaBet) {
        if (bet.strategy?.user?.id != user.id) throw EntityDoesntBelongToUserException()
    }

    private fun discardBetMessages(bet: FifaBet) {
        bet.messages.filter { it.messengerChat != null && it.integrationMessageId != null }
            .forEach { message ->
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