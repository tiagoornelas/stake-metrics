package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaRuleTypes
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.entities.exceptions.IntegrationException
import net.stakemetrics.application.workers.FifaStrategyBettor
import net.stakemetrics.application.workers.OddAndLineCalculator
import net.stakemetrics.application.workers.tipsters.factory.FifaTipster
import net.stakemetrics.application.workers.tipsters.helpers.FifaTipsterHelper
import org.springframework.stereotype.Component

@Component
class FifaGoalLineTipster(
    private val oddAndLineCalculator: OddAndLineCalculator,
    private val fifaStrategyBettor: FifaStrategyBettor,
    private val fifaTipsterHelper: FifaTipsterHelper
) : FifaTipster {
    val notSupportedErrorMessage = "Market's bet candidate not supported for goal line tipster"

    override fun tip(
        betCandidate: FifaMarketBetCandidates,
        matchupPlayerNames: Pair<String, String>,
        rules: MutableSet<FifaStrategyRule>,
        odds: List<FifaDataSourceDTO.FifaGenericOddRequest>,
        results: MutableSet<FifaMatch>
    ) {
        val goalLine = odds.firstOrNull() { it.isGoalLine() }?.toFifaGoalLine()
            ?: throw IntegrationException("No goal lines came from the integrated data source.")

        rules.forEach { rule -> checkRule(betCandidate, rule, goalLine, results) }
        fifaStrategyBettor.bet()
    }

    private fun checkRule(
        betCandidate: FifaMarketBetCandidates,
        rule: FifaStrategyRule,
        line: FifaDataSourceDTO.FifaGoalLineOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        return when (rule.type) {
            FifaRuleTypes.MINIMUM_ODDS -> checkMinimumOddsRule(betCandidate, rule, line)
            FifaRuleTypes.MINIMUM_JUICE -> checkMinimumJuiceRule(betCandidate, rule, line, results)
            FifaRuleTypes.MINIMUM_PROBABILITY -> checkMinimumProbabilityRule(betCandidate, rule, line, results)
            FifaRuleTypes.MINIMUM_MATCHES -> fifaTipsterHelper.checkMinimumMatchesRule(rule, results)
        }
    }

    private fun checkMinimumOddsRule(
        betCandidate: FifaMarketBetCandidates,
        rule: FifaStrategyRule,
        line: FifaDataSourceDTO.FifaGoalLineOddRequest
    ) {
        when (betCandidate) {
            FifaMarketBetCandidates.OVER -> {
                if (line.over < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for over market: ${line.over} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.UNDER -> {
                if (line.under < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for under market: ${line.under} < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException(notSupportedErrorMessage)
        }
    }

    private fun checkMinimumJuiceRule(
        betCandidate: FifaMarketBetCandidates,
        rule: FifaStrategyRule,
        line: FifaDataSourceDTO.FifaGoalLineOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        val threshold = oddAndLineCalculator.getScoreThreshold(line.handicap)
        val (overProbability, underProbability) = getScopeGoalLineProbabilities(results, threshold)

        when (betCandidate) {
            FifaMarketBetCandidates.OVER -> {
                val overFairLine = oddAndLineCalculator.getFairLine(overProbability)
                val overJuice = oddAndLineCalculator.getBettorsJuice(line.over, overFairLine)
                if (overJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for over market: $overJuice < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.UNDER -> {
                val underFairLine = oddAndLineCalculator.getFairLine(underProbability)
                val underJuice = oddAndLineCalculator.getBettorsJuice(line.under, underFairLine)
                if (underJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for under market: $underJuice < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException(notSupportedErrorMessage)
        }
    }

    private fun checkMinimumProbabilityRule(
        betCandidate: FifaMarketBetCandidates,
        rule: FifaStrategyRule,
        line: FifaDataSourceDTO.FifaGoalLineOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        val threshold = oddAndLineCalculator.getScoreThreshold(line.handicap)
        val (overProbability, underProbability) = getScopeGoalLineProbabilities(results, threshold)

        when (betCandidate) {
            FifaMarketBetCandidates.OVER -> {
                if (overProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for over market: $overProbability < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.UNDER -> {
                if (underProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for under market: $underProbability < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException(notSupportedErrorMessage)
        }
    }

    private fun getScopeGoalLineProbabilities(
        results: MutableSet<FifaMatch>,
        threshold: Double
    ): Pair<Double, Double> {
        val totalMatchesInScope: Int = getMatchCount(results)
        val voidMatches: Int = getVoidMatchCount(results, threshold)
        val matchesOverThreshold: Int = getMatchesOverThreshold(results, threshold)
        val matchesUnderThreshold: Int = getMatchesUnderThreshold(results, threshold)

        val accountableTotalMatches = totalMatchesInScope - voidMatches
        val scopeOverProbability = matchesOverThreshold.toDouble() / accountableTotalMatches
        val scopeUnderProbability = matchesUnderThreshold.toDouble() / accountableTotalMatches

        return Pair(scopeOverProbability, scopeUnderProbability)
    }

    private fun getMatchCount(results: MutableSet<FifaMatch>): Int {
        return results.count { it.totalGoalsAtFullTime != null }
    }

    private fun getVoidMatchCount(results: MutableSet<FifaMatch>, threshold: Double): Int {
        return if (threshold % 1.0 == 0.0) {
            results.count { it.totalGoalsAtFullTime != null && it.totalGoalsAtFullTime == threshold.toInt() }
        } else {
            0
        }
    }

    private fun getMatchesOverThreshold(results: MutableSet<FifaMatch>, threshold: Double): Int {
        return results.count { it.totalGoalsAtFullTime != null && it.totalGoalsAtFullTime > threshold }
    }

    private fun getMatchesUnderThreshold(results: MutableSet<FifaMatch>, threshold: Double): Int {
        return results.count { it.totalGoalsAtFullTime != null && it.totalGoalsAtFullTime < threshold }
    }
}