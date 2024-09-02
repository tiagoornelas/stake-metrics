package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import org.springframework.stereotype.Service

@Service
class FifaBetWinnerDeterminer {

    fun determineMatchOddsWinner(
        fifaMatch: FifaMatch,
        candidate: FifaMarketBetCandidates,
        odds: Double
    ): FifaBetDTO.BetResult {
        val winner = when (fifaMatch.winner) {
            fifaMatch.home -> FifaMarketBetCandidates.HOME
            fifaMatch.away -> FifaMarketBetCandidates.AWAY
            else -> FifaMarketBetCandidates.DRAW
        }

        val statusProfitPair = determineStatusAndProfit(candidate, winner, odds)
        val winnerSubType = getWinnerSubType(winner)

        return FifaBetDTO.BetResult(
            winnerSubType = winnerSubType,
            profit = statusProfitPair.second,
            status = statusProfitPair.first
        )
    }

    fun determineGoalLineWinner(
        fifaMatch: FifaMatch,
        candidate: FifaMarketBetCandidates,
        odds: Double,
        handicap: Double
    ): FifaBetDTO.BetResult {
        val totalGoals =
            fifaMatch.totalGoalsAtFullTime?.toDouble() ?: throw IllegalArgumentException("Total goals cannot be null")

        val statusProfitPair = when (candidate) {
            FifaMarketBetCandidates.OVER -> determineGoalLineStatusAndProfit(totalGoals, handicap, odds, true)
            FifaMarketBetCandidates.UNDER -> determineGoalLineStatusAndProfit(totalGoals, handicap, odds, false)
            else -> throw IllegalArgumentException("Invalid candidate for goal line bet: $candidate")
        }

        val winnerSubType = getGoalLineWinnerSubType(candidate, statusProfitPair.first)
        return FifaBetDTO.BetResult(
            winnerSubType = winnerSubType,
            profit = statusProfitPair.second,
            status = statusProfitPair.first
        )
    }

    private fun determineStatusAndProfit(
        candidate: FifaMarketBetCandidates,
        winner: FifaMarketBetCandidates,
        odds: Double
    ): Pair<BetStatusTypes, Double> {
        return if (candidate == winner) {
            BetStatusTypes.WON to (odds - 1.0)
        } else {
            BetStatusTypes.LOST to -1.0
        }
    }

    private fun determineGoalLineStatusAndProfit(
        totalGoals: Double,
        handicap: Double,
        odds: Double,
        isOver: Boolean
    ): Pair<BetStatusTypes, Double> {
        return when {
            isOver && totalGoals > handicap || !isOver && totalGoals < handicap -> BetStatusTypes.WON to (odds - 1.0)
            isOver && totalGoals < handicap || !isOver && totalGoals > handicap -> BetStatusTypes.LOST to -1.0
            else -> BetStatusTypes.VOID to 0.0
        }
    }

    private fun getWinnerSubType(winner: FifaMarketBetCandidates): FifaMarketSubTypes {
        return FifaMarketSubTypes.entries.firstOrNull { it.betCandidates.contains(winner) }
            ?: throw IllegalArgumentException("Invalid winner candidate")
    }

    private fun getGoalLineWinnerSubType(
        candidate: FifaMarketBetCandidates,
        status: BetStatusTypes
    ): FifaMarketSubTypes? {
        val subTypeMap = mapOf(
            BetStatusTypes.WON to candidate,
            BetStatusTypes.HALF_WON to candidate,
            BetStatusTypes.LOST to if (candidate == FifaMarketBetCandidates.OVER) FifaMarketBetCandidates.UNDER else FifaMarketBetCandidates.OVER,
            BetStatusTypes.HALF_LOST to if (candidate == FifaMarketBetCandidates.OVER) FifaMarketBetCandidates.UNDER else FifaMarketBetCandidates.OVER
        )

        return subTypeMap[status]?.let { getWinnerSubType(it) }
    }
}