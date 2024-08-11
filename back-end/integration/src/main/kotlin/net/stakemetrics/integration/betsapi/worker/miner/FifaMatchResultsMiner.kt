package net.stakemetrics.integration.betsapi.worker.miner

import BetsApiSoccerMatchStatus
import java.text.SimpleDateFormat
import java.util.Date
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.integration.betsapi.entities.dtos.BetsApiDTO
import net.stakemetrics.integration.betsapi.utils.BetsApiHttpHelper
import net.stakemetrics.integration.betsapi.utils.FifaMarketHelper
import net.stakemetrics.integration.betsapi.worker.BetsApiRequester
import net.stakemetrics.integration.betsapi.worker.deserializer.MatchDeserializer
import org.springframework.stereotype.Service

@Service
class FifaMatchResultsMiner(
    private val matchDeserializer: MatchDeserializer,
    private val betsApiRequester: BetsApiRequester,
    private val fifaMarketHelper: FifaMarketHelper,
    private val betsApiHttpHelper: BetsApiHttpHelper,
    private val logger: Logger
) {
    fun getFifaMarchResultsForDate(league: FifaLeague, date: Date): List<FifaDataSourceDTO.FifaMatchRequest> {
        val formattedDate = convertDateToString(date)
        val results = mutableListOf<FifaDataSourceDTO.FifaMatchRequest>()
        var page = 1

        do {
            logger.log("[BetsAPI] Fetching Ended Events page $page for league ${league.name} and date $formattedDate")
            val response = betsApiRequester.fetchEndedSoccerEvents(league.integrationId, formattedDate, page)
            val matchResponse = matchDeserializer.parseJsonToMatchResponse(response)
            val pageResults = convertResultListToFifaDtoList(matchResponse.results)
            results.addAll(pageResults)
            page++
        } while (betsApiHttpHelper.hasNext(matchResponse.pager, page))

        return results
    }

    private fun convertResultListToFifaDtoList(results: List<BetsApiDTO.MatchResponse.Result>): List<FifaDataSourceDTO.FifaMatchRequest> {
        return results.filter { it.time_status == BetsApiSoccerMatchStatus.ENDED && it.scores != null }.map { result ->
            val totalGoalsAtHalfTime = calculateTotalGoals(result.scores!!, 1)
            val totalGoalsAtFullTime = calculateTotalGoals(result.scores, 2)
            FifaDataSourceDTO.FifaMatchRequest(
                integrationId = result.id.toLong(),
                time = result.time.toLong(),
                status = result.time_status.toFifaMatchStatusType(),
                leagueId = result.league.id.toLong(),
                home = fifaMarketHelper.getPlayerName(result.home.name),
                away = fifaMarketHelper.getPlayerName(result.away.name),
                homeGoalsAtHalfTime = getGoals(result.scores, 1, "home"),
                homeGoalsAtFullTime = getGoals(result.scores, 2, "home"),
                awayGoalsAtHalfTime = getGoals(result.scores, 1, "away"),
                awayGoalsAtFullTime = getGoals(result.scores, 2, "away"),
                totalGoalsAtHalfTime = totalGoalsAtHalfTime,
                totalGoalsAtFullTime = totalGoalsAtFullTime
            )
        }
    }

    private fun calculateTotalGoals(
        scores: Map<String, BetsApiDTO.Score>,
        period: Int
    ): Int {
        return scores[period.toString()]?.let { it.home.toInt() + it.away.toInt() } ?: 0
    }

    private fun getGoals(
        scores: Map<String, BetsApiDTO.Score>,
        period: Int,
        team: String
    ): Int {
        return scores[period.toString()]?.let {
            if (team == "home") it.home.toInt() else it.away.toInt()
        } ?: 0
    }


    private fun convertDateToString(date: Date): String {
        return SimpleDateFormat("yyyyMMdd").format(date)
    }
}