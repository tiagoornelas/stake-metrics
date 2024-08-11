package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.FifaRuleTypes
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.workers.FifaStrategyBettor
import net.stakemetrics.application.workers.OddAndLineCalculator
import net.stakemetrics.application.workers.tipsters.factory.FifaTipster
import org.springframework.stereotype.Component

@Component
class FifaGoalLineTipster(
    private val oddAndLineCalculator: OddAndLineCalculator,
    private val fifaStrategyBettor: FifaStrategyBettor
) : FifaTipster {
    override fun tip(
        matchupPlayerNames: Pair<String, String>,
        marketSubType: FifaMarketSubTypes,
        rules: MutableSet<FifaStrategyRule>,
        odds: List<FifaDataSourceDTO.FifaGenericOddRequest>,
        results: MutableSet<FifaMatch>
    ) {
        val goalLine = odds.first { it.isGoalLine() }.toFifaGoalLine()

        rules.forEach { rule -> checkRule(marketSubType, rule, goalLine, results) }
        fifaStrategyBettor.bet()
    }

    private fun checkRule(
        marketSubType: FifaMarketSubTypes,
        rule: FifaStrategyRule,
        line: FifaDataSourceDTO.FifaGoalLineOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        return when (rule.type) {
            FifaRuleTypes.MINIMUM_ODDS -> checkMinimumOddsRule(marketSubType, rule, line)
            FifaRuleTypes.MINIMUM_JUICE -> checkMinimumJuiceRule(marketSubType, rule, line, results)
            FifaRuleTypes.MINIMUM_PROBABILITY -> checkMinimumProbabilityRule(marketSubType, rule, line, results)
        }
    }

    private fun checkMinimumOddsRule(
        marketSubType: FifaMarketSubTypes,
        rule: FifaStrategyRule,
        line: FifaDataSourceDTO.FifaGoalLineOddRequest
    ) {
        when (marketSubType) {
            FifaMarketSubTypes.OVER -> {
                if (line.over < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for over market: ${line.over} < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.UNDER -> {
                if (line.under < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for under market: ${line.under} < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException("Market sub type not supported for goal line tipster")
        }
    }

    private fun checkMinimumJuiceRule(
        marketSubType: FifaMarketSubTypes,
        rule: FifaStrategyRule,
        line: FifaDataSourceDTO.FifaGoalLineOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        val threshold = oddAndLineCalculator.getScoreThreshold(line.handicap)
        val (overProbability, underProbability) = getScopeGoalLineProbabilities(results, threshold)

        when (marketSubType) {
            FifaMarketSubTypes.OVER -> {
                val overFairLine = oddAndLineCalculator.getFairLine(overProbability)
                val overJuice = oddAndLineCalculator.getBettorsJuice(line.over, overFairLine)
                if (overJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for over market: $overJuice < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.UNDER -> {
                val underFairLine = oddAndLineCalculator.getFairLine(underProbability)
                val underJuice = oddAndLineCalculator.getBettorsJuice(line.under, underFairLine)
                if (underJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for under market: $underJuice < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException("Market sub type not supported for goal line tipster")
        }
    }

    private fun checkMinimumProbabilityRule(
        marketSubType: FifaMarketSubTypes,
        rule: FifaStrategyRule,
        line: FifaDataSourceDTO.FifaGoalLineOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        val threshold = oddAndLineCalculator.getScoreThreshold(line.handicap)
        val (overProbability, underProbability) = getScopeGoalLineProbabilities(results, threshold)

        when (marketSubType) {
            FifaMarketSubTypes.OVER -> {
                if (overProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for over market: $overProbability < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.UNDER -> {
                if (underProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for under market: $underProbability < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException("Market sub type not supported for goal line tipster")
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