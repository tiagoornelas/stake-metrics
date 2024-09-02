package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.FifaTrendScopeAnalysis
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import org.springframework.stereotype.Component

@Component
class FifaTipsterHelper {

    fun checkMinimumMatchesRule(rule: FifaStrategyRule, analysis: FifaTrendScopeAnalysis) {
        if (analysis.totalMatches < rule.value.toInt()) {
            throw FifaStrategyRuleBreakException(
                "Minimum matches rule break: ${analysis.totalMatches} < ${rule.value}"
            )
        }
    }

    fun getOddForCandidate(
        betCandidate: FifaMarketBetCandidates,
        line: FifaDataSourceDTO.FifaGenericOddRequest
    ): Double {
        return when (betCandidate) {
            FifaMarketBetCandidates.HOME -> line.home!!
            FifaMarketBetCandidates.AWAY -> line.away!!
            FifaMarketBetCandidates.DRAW -> line.draw!!
            FifaMarketBetCandidates.OVER -> line.overGoals!!
            FifaMarketBetCandidates.UNDER -> line.underGoals!!
        }
    }

}