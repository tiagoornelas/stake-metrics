package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.FifaTrendScopeAnalysis
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaRuleTypes
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.workers.tipsters.factory.FifaTipster
import org.springframework.stereotype.Component

@Component
class FifaAsianGoalLineTipster(private val fifaTipsterHelper: FifaTipsterHelper) : FifaTipster {
    val notSupportedErrorMessage = "Market's bet candidate not supported for goal odds tipster"

    override fun analyze(
        betCandidate: FifaMarketBetCandidates,
        rules: MutableSet<FifaStrategyRule>,
        odds: FifaOddSnapshot,
        analysis: FifaTrendScopeAnalysis
    ) {
        for (rule in rules) {
            when (rule.type) {
                FifaRuleTypes.MINIMUM_ODDS -> checkMinimumOddsRule(betCandidate, rule, odds)
                FifaRuleTypes.MINIMUM_JUICE -> checkMinimumJuiceRule(betCandidate, rule, analysis)
                FifaRuleTypes.MINIMUM_PROBABILITY -> checkMinimumProbabilityRule(betCandidate, rule, analysis)
                FifaRuleTypes.MINIMUM_MATCHES -> fifaTipsterHelper.checkMinimumMatchesRule(rule, analysis)
            }
        }
    }

    private fun checkMinimumOddsRule(
        betCandidate: FifaMarketBetCandidates, rule: FifaStrategyRule, odds: FifaOddSnapshot
    ) {
        when (betCandidate) {
            FifaMarketBetCandidates.ASIAN_OVER_GOALS -> {
                if (odds.overGoalsOdd!! < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for over market: ${odds.overGoalsOdd} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.ASIAN_UNDER_GOALS -> {
                if (odds.underGoalsOdd!! < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for under market: ${odds.underGoalsOdd} < ${rule.value}"
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
            FifaMarketBetCandidates.ASIAN_OVER_GOALS -> {
                if (analysis.overJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for over market: ${analysis.overJuice} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.ASIAN_UNDER_GOALS -> {
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
            FifaMarketBetCandidates.ASIAN_OVER_GOALS -> {
                if (analysis.overProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for over market: ${analysis.overProbability} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.ASIAN_UNDER_GOALS -> {
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