package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import net.stakemetrics.application.entities.enums.OddSnapshotTypes
import net.stakemetrics.application.repositories.IFifaOddSnapshotRepository
import org.springframework.stereotype.Service

@Service
class FifaOddSnapshotCloser(
    private val fifaBetWinnerDeterminer: FifaBetWinnerDeterminer,
    private val fifaOddSnapshotRepository: IFifaOddSnapshotRepository
) {

    fun close(fifaOddSnapshot: FifaOddSnapshot) {
        val match = fifaOddSnapshot.fifaMatch
        if (match.status != FifaMatchStatusTypes.ENDED || match.totalGoalsAtFullTime == null) return

        val homeResult = fifaBetWinnerDeterminer.determineMatchOddsWinner(
            match, FifaMarketBetCandidates.HOME,
            fifaOddSnapshot.homeOdd!!
        )
        val drawResult = fifaBetWinnerDeterminer.determineMatchOddsWinner(
            match, FifaMarketBetCandidates.DRAW,
            fifaOddSnapshot.drawOdd!!
        )
        val awayResult = fifaBetWinnerDeterminer.determineMatchOddsWinner(
            match,
            FifaMarketBetCandidates.AWAY,
            fifaOddSnapshot.awayOdd!!
        )

        fifaOddSnapshot.homeProfit = homeResult.profit
        fifaOddSnapshot.drawProfit = drawResult.profit
        fifaOddSnapshot.awayProfit = awayResult.profit

        fifaOddSnapshot.matchOddsWinnerSubType = when {
            homeResult.status == BetStatusTypes.WON -> FifaMarketSubTypes.WINNER
            drawResult.status == BetStatusTypes.WON -> FifaMarketSubTypes.DRAW
            awayResult.status == BetStatusTypes.WON -> FifaMarketSubTypes.WINNER
            else -> null
        }

        val overResult = fifaBetWinnerDeterminer.determineAsianGoalLineWinner(
            match, FifaMarketBetCandidates.ASIAN_OVER_GOALS,
            fifaOddSnapshot.overGoalsOdd!!,
            fifaOddSnapshot.goalsHandicap!!
        )
        val underResult = fifaBetWinnerDeterminer.determineAsianGoalLineWinner(
            match, FifaMarketBetCandidates.ASIAN_UNDER_GOALS,
            fifaOddSnapshot.underGoalsOdd!!,
            fifaOddSnapshot.goalsHandicap
        )

        fifaOddSnapshot.overProfit = overResult.profit
        fifaOddSnapshot.underProfit = underResult.profit

        fifaOddSnapshot.goalLineWinnerSubType = when {
            overResult.status == BetStatusTypes.WON -> FifaMarketSubTypes.ASIAN_OVER_GOALS
            underResult.status == BetStatusTypes.WON -> FifaMarketSubTypes.ASIAN_UNDER_GOALS
            else -> null
        }

        fifaOddSnapshot.status = OddSnapshotTypes.CLOSED

        fifaOddSnapshotRepository.save(fifaOddSnapshot)
    }
}