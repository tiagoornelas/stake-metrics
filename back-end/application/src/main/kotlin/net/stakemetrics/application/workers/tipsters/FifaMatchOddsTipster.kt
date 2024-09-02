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
class FifaMatchOddsTipster(private val fifaTipsterHelper: FifaTipsterHelper) : FifaTipster {
    val notSupportedErrorMessage = "Market's bet candidate not supported for match odds tipster"

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
            FifaMarketBetCandidates.HOME -> {
                if (odds.home!! < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for home market: ${odds.home} < ${rule.value}"
                    )
                }
            }
            FifaMarketBetCandidates.DRAW -> {
                if (odds.draw!! < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for draw market: ${odds.draw} < ${rule.value}"
                    )
                }
            }
            FifaMarketBetCandidates.AWAY -> {
                if (odds.away!! < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for away market: ${odds.draw} < ${rule.value}"
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
            FifaMarketBetCandidates.HOME -> {
                if (analysis.homePlayerJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for home market: ${analysis.homePlayerJuice} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.DRAW -> {
                if (analysis.drawJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for draw market: ${analysis.drawJuice} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.AWAY -> {
                if (analysis.awayPlayerJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for away market: ${analysis.awayPlayerJuice} < ${rule.value}"
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
            FifaMarketBetCandidates.HOME -> {
                if (analysis.homePlayerProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for home market: ${analysis.homePlayerProbability} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.DRAW -> {
                if (analysis.drawProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for draw market: ${analysis.drawProbability} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.AWAY -> {
                if (analysis.drawProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for away market: ${analysis.drawProbability} < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException(notSupportedErrorMessage)
        }
    }

}