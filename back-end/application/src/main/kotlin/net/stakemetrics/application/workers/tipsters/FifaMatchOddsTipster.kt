package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.workers.tipsters.factory.FifaTipster
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.enums.FifaRuleTypes
import net.stakemetrics.application.service.FifaPlayerService
import net.stakemetrics.application.workers.FifaStrategyBettor
import net.stakemetrics.application.workers.OddAndLineCalculator
import org.springframework.stereotype.Component

@Component
class FifaMatchOddsTipster(
    private val oddAndLineCalculator: OddAndLineCalculator,
    private val fifaStrategyBettor: FifaStrategyBettor,
    private val fifaPlayerService: FifaPlayerService
) : FifaTipster {
    override fun tip(
        matchupPlayerNames: Pair<String, String>,
        marketSubType: FifaMarketSubTypes,
        rules: MutableSet<FifaStrategyRule>,
        odds: List<FifaDTO.FifaGenericOddRequest>,
        results: MutableSet<FifaMatch>
    ) {
        if (results.isEmpty()) return
        val matchOddsLine = odds.first { it.isMatchOdds() }.toFifaMatchOddsLine()

        rules.forEach { rule -> checkRule(matchupPlayerNames, marketSubType, rule, matchOddsLine, results) }
        fifaStrategyBettor.bet()
    }

    private fun checkRule(
        matchupPlayerNames: Pair<String, String>,
        marketSubType: FifaMarketSubTypes,
        rule: FifaStrategyRule,
        line: FifaDTO.FifaMatchOddsOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        return when (rule.type) {
            FifaRuleTypes.MINIMUM_ODDS -> checkMinimumOddsRule(marketSubType, rule, line)
            FifaRuleTypes.MINIMUM_JUICE -> checkMinimumJuiceRule(matchupPlayerNames, marketSubType, rule, line, results)
            FifaRuleTypes.MINIMUM_PROBABILITY -> checkMinimumProbabilityRule(
                matchupPlayerNames, marketSubType, rule, results
            )
        }
    }

    private fun checkMinimumOddsRule(
        marketSubType: FifaMarketSubTypes,
        rule: FifaStrategyRule,
        line: FifaDTO.FifaMatchOddsOddRequest
    ) {
        when (marketSubType) {
            FifaMarketSubTypes.HOME -> {
                if (line.home < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for home market: ${line.home} < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.DRAW -> {
                if (line.draw < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for draw market: ${line.draw} < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.AWAY -> {
                if (line.away < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for away market: ${line.away} < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException("Market sub type not supported for match odds tipster")
        }
    }

    private fun checkMinimumJuiceRule(
        matchupPlayerNames: Pair<String, String>,
        marketSubType: FifaMarketSubTypes,
        rule: FifaStrategyRule,
        line: FifaDTO.FifaMatchOddsOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        val (homeProbability, drawProbability, awayProbability) =
            getScopeMatchOddsProbabilities(matchupPlayerNames, results)

        when (marketSubType) {
            FifaMarketSubTypes.HOME -> {
                val homeFairLine = oddAndLineCalculator.getFairLine(homeProbability)
                val homeJuice = oddAndLineCalculator.getBettorsJuice(line.home, homeFairLine)
                if (homeJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for home market: $homeJuice < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.DRAW -> {
                val drawFairLine = oddAndLineCalculator.getFairLine(drawProbability)
                val drawJuice = oddAndLineCalculator.getBettorsJuice(line.draw, drawFairLine)
                if (drawJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for draw market: $drawJuice < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.AWAY -> {
                val awayFairLine = oddAndLineCalculator.getFairLine(awayProbability)
                val awayJuice = oddAndLineCalculator.getBettorsJuice(line.away, awayFairLine)
                if (awayJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for away market: $awayJuice < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException("Market sub type not supported for match odds tipster")
        }
    }

    private fun checkMinimumProbabilityRule(
        matchupPlayerNames: Pair<String, String>,
        marketSubType: FifaMarketSubTypes,
        rule: FifaStrategyRule,
        results: MutableSet<FifaMatch>
    ) {
        val (homeProbability, drawProbability, awayProbability) =
            getScopeMatchOddsProbabilities(matchupPlayerNames, results)

        when (marketSubType) {
            FifaMarketSubTypes.HOME -> {
                if (homeProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for home market: $homeProbability < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.DRAW -> {
                if (drawProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for draw market: $drawProbability < ${rule.value}"
                    )
                }
            }

            FifaMarketSubTypes.AWAY -> {
                if (awayProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for away market: $awayProbability < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException("Market sub type not supported for match odds tipster")
        }
    }

    private fun getScopeMatchOddsProbabilities(
        matchupPlayerNames: Pair<String, String>, results:
        MutableSet<FifaMatch>
    ): Triple<Double, Double, Double> {
        val (homePlayerName, awayPlayerName) = matchupPlayerNames
        val homePlayer = fifaPlayerService.findByName(homePlayerName)
        val awayPlayer = fifaPlayerService.findByName(awayPlayerName)
        val matchCount = getMatchCount(results)
        val drawMatchCount = getDrawMatchCount(results)
        val homePlayerWonMatchesCount = getPlayerWonMatchesCount(results, homePlayer)
        val awayPlayerWonMatchesCount = getPlayerWonMatchesCount(results, awayPlayer)

        val drawProbability = drawMatchCount.toDouble() / matchCount
        val homeWinProbability = homePlayerWonMatchesCount.toDouble() / matchCount
        val awayWinProbability = awayPlayerWonMatchesCount.toDouble() / matchCount

        return Triple(homeWinProbability, drawProbability, awayWinProbability)
    }

    private fun getMatchCount(results: MutableSet<FifaMatch>): Int {
        return results.count { it.status == FifaMatchStatusTypes.ENDED }
    }

    private fun getDrawMatchCount(results: MutableSet<FifaMatch>): Int {
        return results.count { it.status == FifaMatchStatusTypes.ENDED && it.winner == null }
    }

    private fun getPlayerWonMatchesCount(results: MutableSet<FifaMatch>, player: FifaPlayer): Int {
        return results.count { it.status == FifaMatchStatusTypes.ENDED && it.winner == player }
    }
}