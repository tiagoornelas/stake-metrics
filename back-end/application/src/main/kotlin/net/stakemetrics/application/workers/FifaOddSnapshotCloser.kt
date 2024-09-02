package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
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

        val homeResult = fifaBetWinnerDeterminer.determineMatchOddsWinner(
            match, FifaMarketBetCandidates.HOME,
            fifaOddSnapshot.value.home!!
        )
        val drawResult = fifaBetWinnerDeterminer.determineMatchOddsWinner(
            match, FifaMarketBetCandidates.DRAW,
            fifaOddSnapshot.value.draw!!
        )
        val awayResult = fifaBetWinnerDeterminer.determineMatchOddsWinner(
            match,
            FifaMarketBetCandidates.AWAY,
            fifaOddSnapshot.value.away!!
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

        val overResult = fifaBetWinnerDeterminer.determineGoalLineWinner(
            match, FifaMarketBetCandidates.OVER,
            fifaOddSnapshot.value.overGoals!!,
            fifaOddSnapshot.value.goalsHandicap!!
        )
        val underResult = fifaBetWinnerDeterminer.determineGoalLineWinner(
            match, FifaMarketBetCandidates.UNDER,
            fifaOddSnapshot.value.underGoals!!,
            fifaOddSnapshot.value.goalsHandicap
        )

        fifaOddSnapshot.overProfit = overResult.profit
        fifaOddSnapshot.underProfit = underResult.profit

        fifaOddSnapshot.goalLineWinnerSubType = when {
            overResult.status == BetStatusTypes.WON -> FifaMarketSubTypes.OVER
            underResult.status == BetStatusTypes.WON -> FifaMarketSubTypes.UNDER
            else -> null
        }

        fifaOddSnapshot.status = OddSnapshotTypes.CLOSED

        fifaOddSnapshotRepository.save(fifaOddSnapshot)
    }
}