package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import org.springframework.stereotype.Service

@Service
class FifaStrategyScopePicker {

    fun pick(
        strategy: FifaStrategy,
        allScopeResults: MutableSet<FifaStrategyDTO.GenericScopePastResults>
    ): MutableSet<FifaStrategyDTO.FifaStrategyScopePastResults> {
        val results = mutableSetOf<FifaStrategyDTO.FifaStrategyScopePastResults>()

        strategy.scopes.forEach { strategyScope ->
            allScopeResults.filter {
                it.scope?.matchup == strategyScope.matchup &&
                        it.scope?.type == strategyScope.type
            }.forEach { scopeResult ->
                results.add(FifaStrategyDTO.FifaStrategyScopePastResults(
                    scope = strategyScope,
                    pastResults = scopeResult.pastResults
                ))
            }
        }

        return results
    }

}