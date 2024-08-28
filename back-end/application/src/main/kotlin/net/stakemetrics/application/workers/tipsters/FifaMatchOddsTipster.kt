package net.stakemetrics.application.workers.tipsters

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaRuleTypes
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.service.FifaPlayerService
import net.stakemetrics.application.workers.OddAndLineCalculator
import net.stakemetrics.application.workers.tipsters.factory.FifaTipster
import org.springframework.stereotype.Component

@Component
class FifaMatchOddsTipster(
    private val oddAndLineCalculator: OddAndLineCalculator,
    private val fifaPlayerService: FifaPlayerService,
    private val fifaTipsterHelper: FifaTipsterHelper
) : FifaTipster {
    val notSupportedErrorMessage = "Market's bet candidate not supported for match odds tipster"

    override fun getTipstersSpecificLines(odds: List<FifaDataSourceDTO.FifaGenericOddRequest>): FifaDataSourceDTO.FifaGenericOddRequest? {
        return odds.filter { it.isMatchOdds() }.maxByOrNull { it.oddOfferTime }
    }

    override fun analyze(
        betCandidate: FifaMarketBetCandidates,
        matchupPlayerNames: Pair<String, String>,
        rules: MutableSet<FifaStrategyRule>,
        odds: FifaDataSourceDTO.FifaGenericOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        if (results.isEmpty()) return
        val matchOddsLine = odds.toFifaMatchOddsLine()
        rules.forEach { rule -> checkRule(matchupPlayerNames, betCandidate, rule, matchOddsLine, results) }
    }

    private fun checkRule(
        matchupPlayerNames: Pair<String, String>,
        betCandidate: FifaMarketBetCandidates,
        rule: FifaStrategyRule,
        line: FifaDataSourceDTO.FifaMatchOddsOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        return when (rule.type) {
            FifaRuleTypes.MINIMUM_ODDS -> checkMinimumOddsRule(betCandidate, rule, line)
            FifaRuleTypes.MINIMUM_JUICE -> checkMinimumJuiceRule(matchupPlayerNames, betCandidate, rule, line, results)
            FifaRuleTypes.MINIMUM_PROBABILITY -> checkMinimumProbabilityRule(
                matchupPlayerNames,
                betCandidate,
                rule,
                results
            )

            FifaRuleTypes.MINIMUM_MATCHES -> fifaTipsterHelper.checkMinimumMatchesRule(rule, results)
        }
    }

    private fun checkMinimumOddsRule(
        betCandidate: FifaMarketBetCandidates, rule: FifaStrategyRule, line: FifaDataSourceDTO.FifaMatchOddsOddRequest
    ) {
        when (betCandidate) {
            FifaMarketBetCandidates.HOME -> {
                if (line.home < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for home market: ${line.home} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.DRAW -> {
                if (line.draw < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for draw market: ${line.draw} < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.AWAY -> {
                if (line.away < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum odds rule break for away market: ${line.draw} < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException(notSupportedErrorMessage)
        }
    }

    private fun checkMinimumJuiceRule(
        matchupPlayerNames: Pair<String, String>,
        betCandidate: FifaMarketBetCandidates,
        rule: FifaStrategyRule,
        line: FifaDataSourceDTO.FifaMatchOddsOddRequest,
        results: MutableSet<FifaMatch>
    ) {
        val (homeProbability, drawProbability, awayProbability) = getScopeMatchOddsProbabilities(
            matchupPlayerNames,
            results
        )

        when (betCandidate) {
            FifaMarketBetCandidates.HOME -> {
                val homeFairLine = oddAndLineCalculator.getFairLine(homeProbability)
                val homeJuice = oddAndLineCalculator.getBettorsJuice(line.home, homeFairLine)
                if (homeJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for home market: $homeJuice < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.DRAW -> {
                val drawFairLine = oddAndLineCalculator.getFairLine(drawProbability)
                val drawJuice = oddAndLineCalculator.getBettorsJuice(line.draw, drawFairLine)
                if (drawJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for draw market: $drawJuice < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.AWAY -> {
                val awayFairLine = oddAndLineCalculator.getFairLine(awayProbability)
                val awayJuice = oddAndLineCalculator.getBettorsJuice(line.away, awayFairLine)
                if (awayJuice < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum juice rule break for away market: $awayJuice < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException(notSupportedErrorMessage)
        }
    }

    private fun checkMinimumProbabilityRule(
        matchupPlayerNames: Pair<String, String>,
        betCandidate: FifaMarketBetCandidates,
        rule: FifaStrategyRule,
        results: MutableSet<FifaMatch>
    ) {
        val (homeProbability, drawProbability, awayProbability) = getScopeMatchOddsProbabilities(
            matchupPlayerNames,
            results
        )

        when (betCandidate) {
            FifaMarketBetCandidates.HOME -> {
                if (homeProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for home market: $homeProbability < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.DRAW -> {
                if (drawProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for draw market: $drawProbability < ${rule.value}"
                    )
                }
            }

            FifaMarketBetCandidates.AWAY -> {
                if (awayProbability < rule.value) {
                    throw FifaStrategyRuleBreakException(
                        "Minimum probability rule break for away market: $awayProbability < ${rule.value}"
                    )
                }
            }

            else -> throw IllegalArgumentException(notSupportedErrorMessage)
        }
    }

    private fun getScopeMatchOddsProbabilities(
        matchupPlayerNames: Pair<String, String>, results: MutableSet<FifaMatch>
    ): Triple<Double, Double, Double> {
        val (homePlayerName, awayPlayerName) = matchupPlayerNames
        val homePlayer = fifaPlayerService.findByName(homePlayerName)
        val awayPlayer = fifaPlayerService.findByName(awayPlayerName)

        val homePlayerMatches = results.filter { it.home == homePlayer || it.away == homePlayer }.toMutableSet()
        val awayPlayerMatches = results.filter { it.home == awayPlayer || it.away == awayPlayer }.toMutableSet()

        val drawMatchCount = getDrawMatchCount(results)
        val homePlayerWonMatchesCount = getPlayerWonMatchesCount(homePlayerMatches, homePlayer)
        val awayPlayerWonMatchesCount = getPlayerWonMatchesCount(awayPlayerMatches, awayPlayer)

        val drawProbability = drawMatchCount.toDouble() / results.size
        val homeWinProbability = homePlayerWonMatchesCount.toDouble() / homePlayerMatches.size
        val awayWinProbability = awayPlayerWonMatchesCount.toDouble() / awayPlayerMatches.size

        return Triple(homeWinProbability, drawProbability, awayWinProbability)
    }

    private fun getDrawMatchCount(results: MutableSet<FifaMatch>): Int {
        return results.count { it.winner == null }
    }

    private fun getPlayerWonMatchesCount(results: MutableSet<FifaMatch>, player: FifaPlayer): Int {
        return results.count { it.winner == player }
    }
}