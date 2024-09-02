package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.FifaTrendScopeAnalysis
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
        line: FifaOddSnapshot
    ): Double {
        return when (betCandidate) {
            FifaMarketBetCandidates.HOME -> line.homeOdd!!
            FifaMarketBetCandidates.AWAY -> line.awayOdd!!
            FifaMarketBetCandidates.DRAW -> line.drawOdd!!
            FifaMarketBetCandidates.OVER -> line.overGoalsOdd!!
            FifaMarketBetCandidates.UNDER -> line.underGoalsOdd!!
        }
    }

}