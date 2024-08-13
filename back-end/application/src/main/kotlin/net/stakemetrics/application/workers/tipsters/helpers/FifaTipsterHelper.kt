package net.stakemetrics.application.workers.tipsters.helpers

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import org.springframework.stereotype.Component

@Component
class FifaTipsterHelper {

    fun checkMinimumMatchesRule(rule: FifaStrategyRule, results: MutableSet<FifaMatch>) {
        if (results.size < rule.value.toInt()) {
            throw FifaStrategyRuleBreakException(
                "Minimum matches rule break: ${results.size} < ${rule.value}"
            )
        }
    }

}