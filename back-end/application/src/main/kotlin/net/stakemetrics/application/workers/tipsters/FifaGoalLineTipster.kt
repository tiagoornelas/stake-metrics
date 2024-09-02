package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.FifaTrendScopeAnalysis
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaRuleTypes
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.workers.tipsters.factory.FifaTipster
import org.springframework.stereotype.Component

@Component
class FifaGoalLineTipster(private val fifaTipsterHelper: FifaTipsterHelper) : FifaTipster {
    val notSupportedErrorMessage = "Market's bet candidate not supported for goal odds tipster"

    override fun analyze(
        betCandidate: FifaMarketBetCandidates,
        rules: MutableSet<FifaStrategyRule>,
        odds: FifaDataSourceDTO.FifaGenericOddRequest,
        analysis: FifaTrendScopeAnalysis
    ) {
        rules.forEach { rule ->
            return when (rule.type) {
                FifaRuleTypes.MINIMUM_ODDS -> checkMinimumOddsRule(betCandidate, rule, odds)
                FifaRuleTypes.MINIMUM_JUICE -> checkMinimumJuiceRule(betCandidate, rule, analysis)
                FifaRuleTypes.MINIMUM_PROBABILITY -> checkMinimumProbabilityRule(betCandidate, rule, analysis)
                FifaRuleTypes.MINIMUM_MATCHES -> fifaTipsterHelper.checkMinimumMatchesRule(rule, analysis)
            }
        }
    }

    private fun checkMinimumOddsRule(
        betCandidate: FifaMarketBetCandidates, rule: FifaStrategyRule, odds: FifaDataSourceDTO.FifaGenericOddRequest
    ) {
        when (betCandidate) {
            FifaMarketBetCandidates.OVER -> {
                if (odds.overGoals!! < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for over market: ${odds.overGoals} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.UNDER -> {
                if (odds.underGoals!! < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for under market: ${odds.underGoals} < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException(notSupportedErrorMessage)
        }
    }

    private fun checkMinimumJuiceRule(
        betCandidate: FifaMarketBetCandidates,
        rule: FifaStrategyRule,
        analysis: FifaTrendScopeAnalysis
    ) {
        when (betCandidate) {
            FifaMarketBetCandidates.OVER -> {
                if (analysis.overJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for over market: ${analysis.overJuice} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.UNDER -> {
                if (analysis.underJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for under market: ${analysis.underJuice} < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException(notSupportedErrorMessage)
        }
    }

    private fun checkMinimumProbabilityRule(
        betCandidate: FifaMarketBetCandidates,
        rule: FifaStrategyRule,
        analysis: FifaTrendScopeAnalysis
    ) {
        when (betCandidate) {
            FifaMarketBetCandidates.OVER -> {
                if (analysis.overProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for over market: ${analysis.overProbability} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.UNDER -> {
                if (analysis.underProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for under market: ${analysis.underProbability} < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException(notSupportedErrorMessage)
        }
    }

}