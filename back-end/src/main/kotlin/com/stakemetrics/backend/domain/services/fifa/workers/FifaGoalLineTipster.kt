package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.domain.entities.fifa.FifaMatch
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyRule
import com.stakemetrics.backend.domain.enums.fifa.FifaMarketSubTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaRuleTypes
import com.stakemetrics.backend.domain.exceptions.FifaStrategyRuleBreakException
import com.stakemetrics.backend.domain.services.OddAndLineCalculator
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import org.springframework.stereotype.Component

@Component
class FifaGoalLineTipster(
    private val oddAndLineCalculator: OddAndLineCalculator,
    private val fifaStrategyBettor: FifaStrategyBettor,

    ) : FifaTipster {
    override fun tip(
        marketSubType: FifaMarketSubTypes,
        rules: MutableSet<FifaStrategyRule>,
        odds: List<FifaDTO.FifaGenericOddRequest>,
        results: MutableSet<FifaMatch>
    ) {
        val goalLine = odds.first { it.isGoalLine() }.toFifaGoalLine()

        rules.forEach { rule -> checkRule(marketSubType, rule, goalLine, results) }
        fifaStrategyBettor.bet()
    }

    private fun checkRule(
        marketSubType: FifaMarketSubTypes, rule: FifaStrategyRule, goalLine: FifaDTO
        .FifaGoalLineOddRequest, results: MutableSet<FifaMatch>
    ) {
        return when (rule.type) {
            FifaRuleTypes.MINIMUM_ODDS -> checkMinimumOddsRule(marketSubType, rule, goalLine)
            FifaRuleTypes.MINIMUM_JUICE -> checkMinimumJuiceRule(marketSubType, rule, goalLine, results)
            FifaRuleTypes.MINIMUM_PROBABILITY -> checkMinimumProbabilityRule(marketSubType, rule, goalLine, results)
        }
    }

    private fun checkMinimumOddsRule(
        marketSubType: FifaMarketSubTypes,
        rule: FifaStrategyRule,
        goalLine: FifaDTO.FifaGoalLineOddRequest
    ) {
        when (marketSubType) {
            FifaMarketSubTypes.OVER -> {
                if (goalLine.over < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for over market: ${goalLine.over} < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.UNDER -> {
                if (goalLine.under < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for under market: ${goalLine.under} < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException("Market sub type not supported for goal line tipster")
        }
    }

    private fun checkMinimumJuiceRule(
        marketSubType: FifaMarketSubTypes,
        rule: FifaStrategyRule,
        goalLine: FifaDTO.FifaGoalLineOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        val threshold = oddAndLineCalculator.getPointsThreshold(goalLine.handicap)
        val (overGivenProbability, underGivenProbability) = getGivenOverAndUnderProbabilities(goalLine)
        val (overProbability, underProbability) = getScopeOverAndUnderProbabilities(results, threshold)

        val overJuice = oddAndLineCalculator.getBettorsJuice(overGivenProbability, overProbability)
        val underJuice = oddAndLineCalculator.getBettorsJuice(underGivenProbability, underProbability)

        when (marketSubType) {
            FifaMarketSubTypes.OVER -> {
                if (overJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for over market: $overJuice < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.UNDER -> {
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
        goalLine: FifaDTO.FifaGoalLineOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        val threshold = oddAndLineCalculator.getPointsThreshold(goalLine.handicap)
        val (overProbability, underProbability) = getScopeOverAndUnderProbabilities(results, threshold)

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

    private fun getGivenOverAndUnderProbabilities(
        goalLine: FifaDTO.FifaGoalLineOddRequest
    ): Pair<Double, Double> {
        val givenOverProbability = oddAndLineCalculator.getProbability(goalLine.over)
        val givenUnderProbability = oddAndLineCalculator.getProbability(goalLine.under)

        return Pair(givenOverProbability, givenUnderProbability)
    }

    private fun getScopeOverAndUnderProbabilities(
        results: MutableSet<FifaMatch>,
        threshold: Int
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

    private fun getVoidMatchCount(results: MutableSet<FifaMatch>, threshold: Int): Int {
        return results.count { it.totalGoalsAtFullTime != null && it.totalGoalsAtFullTime == threshold }
    }

    private fun getMatchesOverThreshold(results: MutableSet<FifaMatch>, threshold: Int): Int {
        return results.count { it.totalGoalsAtFullTime != null && it.totalGoalsAtFullTime > threshold }
    }

    private fun getMatchesUnderThreshold(results: MutableSet<FifaMatch>, threshold: Int): Int {
        return results.count { it.totalGoalsAtFullTime != null && it.totalGoalsAtFullTime < threshold }
    }
}