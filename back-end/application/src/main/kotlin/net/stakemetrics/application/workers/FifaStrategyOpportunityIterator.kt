package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.FifaTrendScopeAnalysis
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.service.IQueueService
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.application.workers.tipsters.factory.FifaTipster
import org.springframework.stereotype.Service

@Service
class FifaStrategyOpportunityIterator(private val logger: Logger, private val queueService: IQueueService) {

    fun iterate(
        tipster: FifaTipster,
        fifaMatch: FifaMatch,
        strategy: FifaStrategy,
        oddSnapshot: FifaOddSnapshot,
        analysisByScopes: MutableSet<FifaTrendScopeAnalysis>,
    ) {
        val betCandidates = strategy.marketSubTypes.flatMap { it.betCandidates }

        run candidatesAnalysis@{
            betCandidates.forEach { candidate ->
                val ruleBreakErrors = mutableListOf<Exception>()
                analysisByScopes.forEach { scopeAnalysis ->
                    try {
                        val rulesForScope = getRulesForScope(strategy, scopeAnalysis)
                        tipster.analyze(candidate, rulesForScope, oddSnapshot, scopeAnalysis)
                    } catch (e: FifaStrategyRuleBreakException) {
                        logger.logFifaStrategyRuleBreak(e)
                        ruleBreakErrors.add(e)
                    } catch (e: Exception) {
                        logger.logError(e)
                    }
                }

                if (ruleBreakErrors.isEmpty()) {
                    val betRequest  = FifaBetDTO.BetRequest(
                        strategy = strategy,
                        fifaMatchId = fifaMatch.id,
                        candidate = candidate,
                        oddSnapshot = oddSnapshot
                    )
                    
                    queueService.enqueueBetTask(betRequest)
                    return@candidatesAnalysis
                }

            }
        }
    }

    private fun getRulesForScope(
        strategy: FifaStrategy,
        scopeAnalysis: FifaTrendScopeAnalysis
    ): MutableSet<FifaStrategyRule> {
        return strategy.scopes.find { it.matchup === scopeAnalysis.matchup && it.type === scopeAnalysis.type }
            ?.rules ?: mutableSetOf()
    }
}
