package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.domain.exceptions.FifaStrategyRuleBreakException
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import org.springframework.stereotype.Service

@Service
class FifaStrategyAgainstOddsWorker(private val fifaPastResultsSearcher: FifaPastResultsSearcher) {
    fun runStrategyAgainstOdds(request: FifaDTO.FifaStrategyAgainstOddRequest) {
        try {
            val resultsByScopes = fifaPastResultsSearcher.search(
                request.odds.leagueIntegrationId, request.odds.homePlayerName,
                request.odds.awayPlayerName, request.strategy.leagues, request.strategy.scopes
            )
            resultsByScopes.forEach(::println)
        } catch (e: FifaStrategyRuleBreakException) {
            println("FifaStrategy Rule Break caught, we are not betting: ${e.message}")
            return
        }
    }
}