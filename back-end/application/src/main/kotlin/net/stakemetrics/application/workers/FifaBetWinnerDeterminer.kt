package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import org.springframework.stereotype.Service
import kotlin.math.floor

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

    fun determineAsianGoalLineWinner(
        fifaMatch: FifaMatch,
        candidate: FifaMarketBetCandidates,
        odds: Double,
        handicap: Double
    ): FifaBetDTO.BetResult {
        val totalGoals =
            fifaMatch.totalGoalsAtFullTime?.toDouble() ?: throw IllegalArgumentException("Total goals cannot be null")

        val statusProfitPair = when (candidate) {
            FifaMarketBetCandidates.ASIAN_OVER_GOALS -> determineGoalLineStatusAndProfit(totalGoals, handicap, odds, true)
            FifaMarketBetCandidates.ASIAN_UNDER_GOALS -> determineGoalLineStatusAndProfit(totalGoals, handicap, odds, false)
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
        val decimalPart = handicap - floor(handicap)
        
        return when (decimalPart) {
            0.0 -> determineWholeHandicapResult(totalGoals, handicap, odds, isOver)
            0.25 -> determineQuarterHandicapResult(totalGoals, handicap, odds, isOver)
            0.5 -> determineHalfHandicapResult(totalGoals, handicap, odds, isOver)
            0.75 -> determineThreeQuarterHandicapResult(totalGoals, handicap, odds, isOver)
            else -> throw IllegalArgumentException("Invalid handicap value: $handicap")
        }
    }

    private fun determineWholeHandicapResult(
        totalGoals: Double,
        handicap: Double,
        odds: Double,
        isOver: Boolean
    ): Pair<BetStatusTypes, Double> = when {
        isOver && totalGoals > handicap -> BetStatusTypes.WON to (odds - 1.0)
        !isOver && totalGoals < handicap -> BetStatusTypes.WON to (odds - 1.0)
        totalGoals == handicap -> BetStatusTypes.VOID to 0.0
        else -> BetStatusTypes.LOST to -1.0
    }

    private fun determineQuarterHandicapResult(
        totalGoals: Double,
        handicap: Double,
        odds: Double,
        isOver: Boolean
    ): Pair<BetStatusTypes, Double> {
        val wholeNumber = floor(handicap)
        return when {
            isOver && totalGoals > wholeNumber + 0.5 -> BetStatusTypes.WON to (odds - 1.0)
            isOver && totalGoals == wholeNumber -> BetStatusTypes.HALF_LOST to -0.5
            !isOver && totalGoals < wholeNumber -> BetStatusTypes.WON to (odds - 1.0)
            !isOver && totalGoals == wholeNumber -> BetStatusTypes.HALF_WON to (odds - 1.0) / 2
            else -> BetStatusTypes.LOST to -1.0
        }
    }

    private fun determineHalfHandicapResult(
        totalGoals: Double,
        handicap: Double,
        odds: Double,
        isOver: Boolean
    ): Pair<BetStatusTypes, Double> = when {
        isOver && totalGoals > handicap -> BetStatusTypes.WON to (odds - 1.0)
        !isOver && totalGoals < handicap -> BetStatusTypes.WON to (odds - 1.0)
        else -> BetStatusTypes.LOST to -1.0
    }

    private fun determineThreeQuarterHandicapResult(
        totalGoals: Double,
        handicap: Double,
        odds: Double,
        isOver: Boolean
    ): Pair<BetStatusTypes, Double> {
        val wholeNumber = floor(handicap)
        return when {
            isOver && totalGoals > wholeNumber + 1 -> BetStatusTypes.WON to (odds - 1.0)
            isOver && totalGoals == wholeNumber + 1 -> BetStatusTypes.HALF_WON to (odds - 1.0) / 2
            !isOver && totalGoals < wholeNumber + 1 -> BetStatusTypes.WON to (odds - 1.0)
            !isOver && totalGoals == wholeNumber + 1 -> BetStatusTypes.HALF_LOST to -0.5
            else -> BetStatusTypes.LOST to -1.0
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
            BetStatusTypes.LOST to if (candidate == FifaMarketBetCandidates.ASIAN_OVER_GOALS) FifaMarketBetCandidates.ASIAN_UNDER_GOALS else FifaMarketBetCandidates.ASIAN_OVER_GOALS,
            BetStatusTypes.HALF_LOST to if (candidate == FifaMarketBetCandidates.ASIAN_OVER_GOALS) FifaMarketBetCandidates.ASIAN_UNDER_GOALS else FifaMarketBetCandidates.ASIAN_OVER_GOALS
        )

        return subTypeMap[status]?.let { getWinnerSubType(it) }
    }
}