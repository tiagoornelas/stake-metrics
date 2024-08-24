package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.workers.tipsters.factory.FifaBetCloser
import org.springframework.stereotype.Service

@Service
class FifaMatchOddsBetCloser(private val fifaBetRepository: IFifaBetRepository) : FifaBetCloser {

    override fun closeBet(fifaBet: FifaBet): FifaBet {
        val match = fifaBet.match!!
        val candidate = fifaBet.line

        val winner = when (match.winner) {
            match.home -> FifaMarketBetCandidates.HOME
            match.away -> FifaMarketBetCandidates.AWAY
            else -> FifaMarketBetCandidates.DRAW
        }

        if (candidate == winner) {
            fifaBet.status = BetStatusTypes.WON
            fifaBet.profit = fifaBet.odds - 1.0
        } else {
            fifaBet.status = BetStatusTypes.LOST
            fifaBet.profit = -1.0
        }

        fifaBetRepository.save(fifaBet)
        return fifaBet
    }

}