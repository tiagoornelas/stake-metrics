package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.domain.exceptions.FifaStrategyRuleBreakException
import com.stakemetrics.backend.domain.ports.LoggerPort
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import org.springframework.stereotype.Service

@Service
class FifaStrategyAgainstOddsWorker(
    private val fifaPastResultsSearcher: FifaPastResultsSearcher,
    private val fifaTipsterFactory: FifaTipsterFactory,
    private val loggerPort: LoggerPort
) {
    fun runStrategyAgainstOdds(request: FifaDTO.FifaStrategyAgainstOddRequest) {
        val resultsByScopes = fifaPastResultsSearcher.search(
            request.odds.leagueIntegrationId, request.odds.homePlayerName,
            request.odds.awayPlayerName, request.strategy.leagues, request.strategy.scopes
        )

        val tipster = fifaTipsterFactory.getTipster(request.strategy.marketType)
        iterateOverOpportunitiesToTipster(resultsByScopes, request, tipster)
    }

    private fun iterateOverOpportunitiesToTipster(
        resultsByScopes: MutableSet<FifaStrategyScopePastResults>,
        request: FifaDTO.FifaStrategyAgainstOddRequest,
        tipster: FifaTipster
    ) {
        val matchupPlayerNames: Pair<String, String> = Pair(request.odds.homePlayerName, request.odds.awayPlayerName)

        resultsByScopes.forEach { (scope, results) ->
            scope?.let {
                request.strategy.marketSubTypes.forEach { marketSubType ->
                    try {
                        tipster.tip(matchupPlayerNames, marketSubType, scope.rules, request.odds.odds, results)
                    } catch (e: FifaStrategyRuleBreakException) {
                        loggerPort.logFifaStrategyRuleBreak(e)
                        return
                    }
                }
            } ?: throw IllegalArgumentException("Scope is null when sending the results to the tipster")
        }
    }

}