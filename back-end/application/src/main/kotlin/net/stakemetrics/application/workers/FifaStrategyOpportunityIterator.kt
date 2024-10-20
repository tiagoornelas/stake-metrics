package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.*
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.application.workers.tipsters.factory.FifaTipster
import org.springframework.stereotype.Service

@Service
class FifaStrategyOpportunityIterator(private val logger: Logger) {

    fun iterate(
        tipster: FifaTipster,
        fifaMatch: FifaMatch,
        strategy: FifaStrategy,
        oddSnapshot: FifaOddSnapshot,
        analysisByScopes: MutableSet<FifaTrendScopeAnalysis>,
    ): FifaMarketBetCandidates? {
        val betCandidates = strategy.marketSubTypes.flatMap { it.betCandidates }

        return betCandidates.firstOrNull { candidate ->
            analysisByScopes.all { scopeAnalysis ->
                try {
                    val rulesForScope = getRulesForScope(strategy, scopeAnalysis)
                    tipster.analyze(candidate, rulesForScope, oddSnapshot, scopeAnalysis)
                    true
                } catch (e: FifaStrategyRuleBreakException) {
                    logger.logFifaStrategyRuleBreak(e)
                    false
                } catch (e: Exception) {
                    logger.logError(e)
                    false
                }
            }
        }
    }

    private fun getRulesForScope(
        strategy: FifaStrategy, scopeAnalysis: FifaTrendScopeAnalysis
    ): MutableSet<FifaStrategyRule> {
        return strategy.scopes.find { it.matchup === scopeAnalysis.matchup && it.type === scopeAnalysis.type }?.rules
            ?: mutableSetOf()
    }
}
