package net.stakemetrics.application.workers

import java.util.Date
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.service.FifaBetService
import net.stakemetrics.application.service.FifaLeagueService
import net.stakemetrics.application.service.FifaMatchService
import net.stakemetrics.application.service.FifaPlayerService
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.application.workers.tipsters.helpers.FifaTipsterHelper
import org.springframework.stereotype.Component

@Component
class FifaStrategyBettor(
    private val logger: Logger,
    private val fifaBetService: FifaBetService,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaPlayerService: FifaPlayerService,
    private val fifaTipsterHelper: FifaTipsterHelper,
    private val fifaMatchService: FifaMatchService,
) {

    fun buildAndBet(
        request: FifaStrategyDTO.FifaStrategyAgainstOddRequest,
        candidate: FifaMarketBetCandidates,
        lineOdds: FifaDataSourceDTO.FifaGenericOddRequest
    ) {
        val fifaLeague = fifaLeagueService.findByIntegrationId(request.odds.leagueIntegrationId)
        val home = fifaPlayerService.findByName(request.odds.homePlayerName)
        val away = fifaPlayerService.findByName(request.odds.awayPlayerName)

        val fifaMatch = try {
            fifaMatchService.findByIntegrationId(request.odds.matchIntegrationId)
        } catch (e: NotFoundException) {
            FifaMatch(
                integrationId = request.odds.matchIntegrationId,
                time = request.odds.odds.first().matchTime,
                league = fifaLeague,
                home = home,
                away = away
            )
        }


        val fifaBet = FifaBet(
            isPaperBet = request.strategy.isPaperBetting,
            strategy = request.strategy,
            match = fifaMatch,
            line = candidate,
            messages = mutableListOf(),
            odds = fifaTipsterHelper.getOddForCandidate(candidate, lineOdds),
            oddOfferTime = lineOdds.oddOfferTime,
            betTime = Date()
        )

        bet(fifaBet)
    }

    fun bet(fifaBet: FifaBet) {
        val alreadyBet = fifaBetService.existsByStrategyAndMatch(fifaBet.strategy!!, fifaBet.match!!)
        if (alreadyBet) {
            logger.log("Already bet on the match ${fifaBet.match.integrationId} with the strategy ${fifaBet.strategy.id}")
            return
        }

        fifaBetService.save(fifaBet)
        if (!fifaBet.isPaperBet) notifyUser()
        println("Betting on Fifa Strategy")
    }

    private fun notifyUser() {
        println("Notifying user")
    }
}