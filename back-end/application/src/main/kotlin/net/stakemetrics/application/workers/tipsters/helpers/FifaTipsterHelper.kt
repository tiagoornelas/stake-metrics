package net.stakemetrics.application.workers.tipsters.helpers

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.entities.exceptions.InternalLogicException
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

    fun getOddForCandidate(betCandidate: FifaMarketBetCandidates, line: FifaDataSourceDTO.FifaGenericOddRequest): Double {
        val noOddsErrorMessage = "Could not find odds for candidate with the given generic line"

        return when (betCandidate) {
            FifaMarketBetCandidates.HOME -> line.home ?: throw InternalLogicException(noOddsErrorMessage)
            FifaMarketBetCandidates.AWAY -> line.away ?: throw InternalLogicException(noOddsErrorMessage)
            FifaMarketBetCandidates.DRAW -> line.draw ?: throw InternalLogicException(noOddsErrorMessage)
            FifaMarketBetCandidates.OVER -> line.over ?: throw InternalLogicException(noOddsErrorMessage)
            FifaMarketBetCandidates.UNDER -> line.under ?: throw InternalLogicException(noOddsErrorMessage)
        }
    }

}