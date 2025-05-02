package net.stakemetrics.application.repositories

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

interface IFifaBetRepository {
    fun save(fifaBet: FifaBet)
    fun delete(fifaBet: FifaBet)
    fun findById(id: UUID): FifaBet
    fun findOpenBets(): List<FifaBet>
    fun countOpenBetsByUser(userId: UUID): Int
    fun existsByStrategyAndMatch(fifaStrategy: FifaStrategy, fifaMatch: FifaMatch): Boolean
    fun findBetsByStrategyAndFilter(
        userEmail: String,
        strategyId: UUID,
        betFilter: FifaBetDTO.BetFilter,
        pageable: Pageable
    ): Page<FifaBet>

    fun listCumulativeProfits(strategyId: UUID): List<Double>
    fun deleteAllByStrategyId(strategyId: UUID)
    fun findByMessengerChatAndDateBetween(
        messengerChatId: UUID,
        timezone: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<FifaBet>

    fun getMainStatisticsByUserAndDateBetween(
        userId: UUID,
        timezone: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): FifaBetDTO.MainStatistics

    fun getMonthlyProfits(
        userId: UUID,
        timezone: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<FifaBetDTO.MonthlyProfit>

    fun getDailyProfits(
        userId: UUID,
        timezone: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<FifaBetDTO.DailyProfit>

    fun getPossibleProfitFromOpenBets(userId: UUID): Double
    fun findByStrategyIdAndBetTimeAfter(strategyId: UUID, betTime: Date): List<FifaStrategyDTO.SimpleReportBet>
    fun findDetailedBetsByStrategyIdAndBetTimeAfter(strategyId: UUID, date: Date): List<FifaStrategyDTO.DetailedReportBet>
    fun existsNonPaperBetByUserAndMatchAndLine(userId: UUID, matchId: UUID, line: FifaMarketBetCandidates): Boolean
}