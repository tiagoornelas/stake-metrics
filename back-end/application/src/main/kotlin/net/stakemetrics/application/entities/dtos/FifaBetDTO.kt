package net.stakemetrics.application.entities.dtos

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import java.time.LocalDate

class FifaBetDTO {

    data class BetRequest(
        val strategy: FifaStrategy,
        val fifaMatchId: UUID,
        val candidate: FifaMarketBetCandidates,
        val oddSnapshot: FifaOddSnapshot
    )

    data class BetResponse(
        val id: UUID,
        val isPaperBet: Boolean,
        val strategyName: String,
        val leagueName: String,
        val homePlayerName: String,
        val awayPlayerName: String,
        val matchTime: Date,
        val betTime: Date,
        val candidate: FifaMarketBetCandidates,
        val odds: Double,
        val status: BetStatusTypes,
        val score: String?,
        val handicap: Double?,
        val profit: Double?,
    )

    data class DeleteResponse(
        val success: Boolean = true
    )

    data class CloseBetRequest(
        val bet: FifaBet,
    )

    data class BetResult(
        val winnerSubType: FifaMarketSubTypes?,
        val profit: Double,
        val status: BetStatusTypes
    )

    data class BetFilter(
        val openBets: Boolean,
        val closedBets: Boolean,
        val paperBets: Boolean,
        val realBets: Boolean,
        val league: List<UUID>,
        val page: Int,
        val size: Int
    )

    data class DailyProfit(val date: LocalDate, val profit: Double)
    data class MonthlyProfit(val startDate: LocalDate, val endDate: LocalDate, val profit: Double)

    data class MainStatistics(
        val numberOfBets: Long,
        val profit: Double,
        val roi: Double
    )

    data class StatisticsResponse(
        val openBets: Int,
        val possibleProfitOnOpenBets: Double,
        val dayStatistics: MainStatistics,
        val monthStatistics: MainStatistics,
        val last12DaysProfit: List<DailyProfit>,
        val last12MonthsProfit: List<MonthlyProfit>
    )

}

fun FifaBet.toResponse(): FifaBetDTO.BetResponse {
    val score = this.match?.let { match ->
        if (match.homeGoalsAtFullTime != null && match.awayGoalsAtFullTime != null) {
            "${match.homeGoalsAtFullTime} x ${match.awayGoalsAtFullTime}"
        } else {
            null
        }
    }

    return FifaBetDTO.BetResponse(
        id = this.id,
        isPaperBet = this.isPaperBet,
        strategyName = this.strategy?.name ?: "",
        leagueName = this.match?.league?.name ?: "",
        homePlayerName = this.match?.home?.name ?: "",
        awayPlayerName = this.match?.away?.name ?: "",
        matchTime = this.match?.time ?: Date(),
        betTime = this.betTime,
        candidate = this.line,
        odds = this.odds,
        status = this.status,
        score = score,
        handicap = this.handicap,
        profit = this.profit
    )
}