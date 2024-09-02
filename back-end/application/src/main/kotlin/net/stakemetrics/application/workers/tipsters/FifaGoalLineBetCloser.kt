package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.workers.FifaBetWinnerDeterminer
import net.stakemetrics.application.workers.tipsters.factory.FifaBetCloser
import org.springframework.stereotype.Service

@Service
class FifaGoalLineBetCloser(
    private val fifaBetRepository: IFifaBetRepository,
    private val fifaBetWinnerDeterminer: FifaBetWinnerDeterminer
) : FifaBetCloser {

    override fun closeBet(fifaBet: FifaBet): FifaBet {
        val candidate = fifaBet.line
        val match = fifaBet.match!!
        val handicap = fifaBet.handicap!!
        val result = fifaBetWinnerDeterminer.determineGoalLineWinner(match, candidate, fifaBet.odds, handicap)

        fifaBet.status = result.status
        fifaBet.profit = result.profit
        fifaBetRepository.save(fifaBet)
        return fifaBet
    }

}