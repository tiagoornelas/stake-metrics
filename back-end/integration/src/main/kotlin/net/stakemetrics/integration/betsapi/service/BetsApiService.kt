package net.stakemetrics.integration.betsapi.service

import BetsApiEndedScoresDeserializer
import BetsApiSoccerMatchStatus
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.service.IFifaIntegratedDataSourceService
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.integration.betsapi.entities.dtos.BetsApiDTO
import net.stakemetrics.integration.betsapi.worker.BetsApiRequester
import org.springframework.stereotype.Service

@Service
class BetsApiService(
    private val betsApiRequester: BetsApiRequester,
    private val logger: Logger
) : IFifaIntegratedDataSourceService {

    override fun getFifaMatchResultsForLeagueSinceDate(league: FifaLeague, date: Date): List<FifaDTO.FifaMatchRequest> {
        val miningDateArrays = getMiningDateRange(date)
        val results = mutableListOf<FifaDTO.FifaMatchRequest>()
        miningDateArrays.forEach { miningDate ->
            results.addAll(getFifaMarchResultsForDate(league, miningDate))
        }
        return results
    }

    override fun getUpcomingFifaMatchesWithOddsForLeague(league: FifaLeague): List<FifaDTO.FifaOddRequest> {
        TODO("Not yet implemented")
    }

    private fun getFifaMarchResultsForDate(league: FifaLeague, date: Date): List<FifaDTO.FifaMatchRequest> {
        val formattedDate = convertDateToString(date)
        val results = mutableListOf<FifaDTO.FifaMatchRequest>()
        var page = 1

        do {
            logger.log("Fetching BetsAPI page $page for league ${league.name} on $formattedDate")
            val response = betsApiRequester.fetchEndedSoccerEvents(league.integrationId, formattedDate, page)
            val endedScoresResponse = parseJsonToEndedScoresResponse(response)
            val pageResults = convertResultListToFifaDtoList(endedScoresResponse.results)
            results.addAll(pageResults)
            page++
        } while (hasNext(endedScoresResponse, page))

        return results
    }

    private fun convertDateToString(date: Date): String {
        return SimpleDateFormat("yyyyMMdd").format(date)
    }

    private fun parseJsonToEndedScoresResponse(response: JsonObject): BetsApiDTO.EndedScoresResponse {
        val gson = GsonBuilder()
            .registerTypeAdapter(BetsApiDTO.EndedScoresResponse::class.java, BetsApiEndedScoresDeserializer())
            .create()
        return gson.fromJson(response, BetsApiDTO.EndedScoresResponse::class.java)
    }

    private fun convertResultListToFifaDtoList(results: List<BetsApiDTO.EndedScoresResponse.Result>): List<FifaDTO.FifaMatchRequest> {
        return results.filter { it.time_status == BetsApiSoccerMatchStatus.ENDED }.map { result ->
            val totalGoalsAtHalfTime = calculateTotalGoals(result.scores, "1")
            val totalGoalsAtFullTime = calculateTotalGoals(result.scores, "2")
            FifaDTO.FifaMatchRequest(
                integrationId = result.id.toInt(),
                time = result.time.toInt(),
                status = result.time_status.toFifaMatchStatusType(),
                leagueId = result.league.id.toInt(),
                home = getPlayerName(result.home.name),
                away = getPlayerName(result.away.name),
                homeGoalsAtHalfTime = getGoals(result.scores, "1", "home"),
                homeGoalsAtFullTime = getGoals(result.scores, "2", "home"),
                awayGoalsAtHalfTime = getGoals(result.scores, "1", "away"),
                awayGoalsAtFullTime = getGoals(result.scores, "2", "away"),
                totalGoalsAtHalfTime = totalGoalsAtHalfTime,
                totalGoalsAtFullTime = totalGoalsAtFullTime
            )
        }
    }

    private fun getPlayerName(defaultName: String): String {
        return defaultName.split("(").last().split(")").first().trim()
    }

    private fun calculateTotalGoals(
        scores: Map<String, BetsApiDTO.Score>,
        period: String
    ): Int {
        return scores[period]?.let { it.home.toInt() + it.away.toInt() } ?: 0
    }

    private fun getGoals(
        scores: Map<String, BetsApiDTO.Score>,
        period: String,
        team: String
    ): Int {
        return scores[period]?.let {
            if (team == "home") it.home.toInt() else it.away.toInt()
        } ?: 0
    }

    private fun hasNext(endedScoresResponse: BetsApiDTO.EndedScoresResponse, page: Int): Boolean {
        val totalFetched = (page - 1) * endedScoresResponse.pager.per_page
        return endedScoresResponse.pager.total > totalFetched
    }

    private fun getMiningDateRange(sinceDate: Date): List<Date> {
        val today = Calendar.getInstance().time

        val dateList = mutableListOf<Date>()
        val calendar = Calendar.getInstance().apply {
            time = sinceDate
        }

        while (!calendar.time.after(today)) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dateList
    }
}
