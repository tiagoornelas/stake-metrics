package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.service.IQueueService
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.application.workers.tipsters.factory.FifaTipster
import org.springframework.stereotype.Service

@Service
class FifaStrategyOpportunityIterator(private val logger: Logger, private val queueService: IQueueService) {

    fun iterate(
        resultsByScopes: MutableSet<FifaStrategyDTO.FifaStrategyScopePastResults>,
        request: FifaStrategyDTO.FifaStrategyAgainstOddRequest,
        tipster: FifaTipster
    ) {
        val matchupPlayerNames: Pair<String, String> = Pair(request.odds.homePlayerName, request.odds.awayPlayerName)
        val betCandidates = request.strategy.marketSubTypes.flatMap { it.betCandidates }

        run candidatesAnalysis@{
            betCandidates.forEach { candidate ->
                val ruleBreakErrors = mutableListOf<Exception>()
                resultsByScopes.forEach { (scope, results) ->
                    scope?.let {
                        try {
                            val specificLine = tipster.getTipstersSpecificLines(request.odds.odds)
                            if (specificLine == null) {
                                logger.log("No specific line found for tipster")
                                return@candidatesAnalysis
                            } else {
                                tipster.analyze(candidate, matchupPlayerNames, scope.rules, specificLine, results)
                            }
                        } catch (e: FifaStrategyRuleBreakException) {
                            logger.logFifaStrategyRuleBreak(e)
                            ruleBreakErrors.add(e)
                        } catch (e: Exception) {
                            logger.logError(e)
                        }
                    } ?: throw IllegalArgumentException("Scope is null when sending the results to the tipster")
                }

                if (ruleBreakErrors.isEmpty()) {
                    val specificLine = tipster.getTipstersSpecificLines(request.odds.odds)
                    if (specificLine == null) {
                        logger.log("No specific line found for tipster")
                        return@candidatesAnalysis
                    } else {
                        queueService.enqueueBetTask(
                            FifaBetDTO.BetRequest(
                                strategy = request.strategy,
                                leagueIntegrationId = request.odds.leagueIntegrationId,
                                homePlayerName = request.odds.homePlayerName,
                                awayPlayerName = request.odds.awayPlayerName,
                                matchIntegrationId = request.odds.matchIntegrationId,
                                matchTime = specificLine.matchTime,
                                candidate = candidate,
                                lineOdds = specificLine
                            )
                        )
                        return@candidatesAnalysis
                    }

                }
            }
        }
    }
}