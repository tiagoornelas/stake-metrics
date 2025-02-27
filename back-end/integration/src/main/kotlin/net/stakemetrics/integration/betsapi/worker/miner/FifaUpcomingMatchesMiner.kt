package net.stakemetrics.integration.betsapi.worker.miner

import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.integration.betsapi.entities.dtos.BetsApiDTO
import net.stakemetrics.integration.betsapi.utils.BetsApiHelper
import net.stakemetrics.integration.betsapi.utils.FifaMarketHelper
import net.stakemetrics.integration.betsapi.worker.BetsApiRequester
import net.stakemetrics.integration.betsapi.worker.deserializer.MatchDeserializer
import net.stakemetrics.integration.betsapi.worker.deserializer.OddDeserializer
import org.springframework.stereotype.Service

@Service
class FifaUpcomingMatchesMiner(
    private val matchDeserializer: MatchDeserializer,
    private val oddDeserializer: OddDeserializer,
    private val betsApiRequester: BetsApiRequester,
    private val fifaMarketHelper: FifaMarketHelper,
    private val betsApiHelper: BetsApiHelper,
    private val logger: Logger
) {
    fun getUpcomingFifaMatchesWithOddsForLeague(league: FifaLeague): List<FifaDataSourceDTO.FifaOddRequest> {
        val results = mutableListOf<FifaDataSourceDTO.FifaOddRequest>()
        var page = 1

        do {
            logger.log("[BetsAPI] Fetching Upcoming Events page $page for league ${league.name}")
            val response = betsApiRequester.fetchUpcomingSoccerEvents(league.integrationId, page)
            val matchResponse = matchDeserializer.parseJsonToMatchResponse(response)
            val pageResults = convertResultListToFifaDtoList(matchResponse.results)
            results.addAll(pageResults)
            page++
        } while (betsApiHelper.hasNext(matchResponse.pager, page))

        return results
    }

    private fun convertResultListToFifaDtoList(results: List<BetsApiDTO.MatchResponse.Result>): List<FifaDataSourceDTO.FifaOddRequest> {
        val filteredResults = filterResultsWithinOneHour(results)

        return filteredResults.mapNotNull { result ->
            logger.log("[BetsAPI] Fetching Event Odds for match ${result.id}")
            val response = betsApiRequester.fetchOddsForMatch(result.id.toInt())
            val oddResponse = oddDeserializer.parseJsonToOddResponse(response)
            val odds = convertResultListToFifaGenericOddsList(oddResponse, result.time)

            odds?.let {
                FifaDataSourceDTO.FifaOddRequest(
                    leagueIntegrationId = result.league.id.toLong(),
                    matchIntegrationId = result.id.toLong(),
                    bet365Id = result.bet365_id?.toLongOrNull(),
                    homePlayerName = fifaMarketHelper.getPlayerName(result.home.name),
                    awayPlayerName = fifaMarketHelper.getPlayerName(result.away.name),
                    odds = it
                )
            }
        }
    }

    private fun filterResultsWithinOneHour(results: List<BetsApiDTO.MatchResponse.Result>): List<BetsApiDTO.MatchResponse.Result> {
        val currentTime = System.currentTimeMillis() / 1000
        val oneHourFromNow = currentTime + 3600

        return results.filter { result ->
            result.time.toLong() in currentTime..oneHourFromNow
        }
    }

    private fun convertResultListToFifaGenericOddsList(oddResponse: BetsApiDTO.EventOddsResponse, matchTime: String):
            FifaDataSourceDTO.FifaGenericOddRequest? {
        val applicationMarketTypes = oddResponse.odds.keys.filter { it.applicationType != null }

        val validMarketTypes = applicationMarketTypes.filter { marketType ->
            oddResponse.odds[marketType]?.isNotEmpty() == true
        }

        if (validMarketTypes.isNotEmpty()) {
            val latestOdds = validMarketTypes.mapNotNull { marketType ->
                val oddsList = oddResponse.odds[marketType] ?: return@mapNotNull null
                val latestOdd = oddsList.maxByOrNull { it.add_time.toLong() } ?: return@mapNotNull null
                marketType.applicationType to latestOdd
            }.toMap()

            val matchOdds = latestOdds[FifaMarketTypes.MATCH_ODDS]
            val goalLineOdds = latestOdds[FifaMarketTypes.ASIAN_GOAL_LINE]

            if (matchOdds != null && goalLineOdds != null) {
                val home = matchOdds.home_od?.toDouble()
                val draw = matchOdds.draw_od?.toDouble()
                val away = matchOdds.away_od?.toDouble()
                val over = goalLineOdds.over_od?.toDouble()
                val under = goalLineOdds.under_od?.toDouble()
                val handicap = goalLineOdds.handicap?.let { fifaMarketHelper.getHandicap(it) }

                if (home != null && draw != null && away != null && over != null && under != null && handicap != null) {
                    return FifaDataSourceDTO.FifaGenericOddRequest(
                        matchTime = betsApiHelper.convertTimestampToDate(matchTime),
                        home = home,
                        draw = draw,
                        away = away,
                        overGoals = over,
                        underGoals = under,
                        goalsHandicap = handicap
                    )
                } else {
                    logger.log(
                        "[UpcomingMatchesMiner] Missing required odds for match $matchTime: home=$home, " +
                                "draw=$draw, away=$away, over=$over, under=$under, handicap=$handicap"
                    )
                }
            }
        }

        return null
    }
}