package net.stakemetrics.integration.betsapi.worker.miner

import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
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

        return filteredResults.map { result ->
            logger.log("[BetsAPI] Fetching Event Odds for match ${result.id}")
            val response = betsApiRequester.fetchOddsForMatch(result.id.toInt())
            val oddResponse = oddDeserializer.parseJsonToOddResponse(response)
            val odds = convertResultListToFifaGenericOddsList(oddResponse, result.time)

            FifaDataSourceDTO.FifaOddRequest(
                leagueIntegrationId = result.league.id.toLong(),
                homePlayerName = fifaMarketHelper.getPlayerName(result.home.name),
                awayPlayerName = fifaMarketHelper.getPlayerName(result.away.name),
                odds = odds
            )
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
            List<FifaDataSourceDTO.FifaGenericOddRequest> {
        val fifaGenericOddRequests = mutableListOf<FifaDataSourceDTO.FifaGenericOddRequest>()

        oddResponse.odds.forEach { (marketType, oddsList) ->
            val updateTime = oddResponse.stats.odds_update[marketType]
            val fifaMarketTypeOnApplication = marketType.applicationType

            if (fifaMarketTypeOnApplication != null) {
                oddsList.forEach { odd ->
                    fifaGenericOddRequests.add(
                        FifaDataSourceDTO.FifaGenericOddRequest(
                            marketType = fifaMarketTypeOnApplication,
                            lastCheckedTime = updateTime?.let { betsApiHelper.convertTimestampToDate(it) },
                            oddOfferTime = betsApiHelper.convertTimestampToDate(odd.add_time),
                            matchTime = betsApiHelper.convertTimestampToDate(matchTime),
                            handicap = odd.handicap?.let { fifaMarketHelper.getHandicap(it) },
                            over = odd.over_od?.toDouble(),
                            under = odd.under_od?.toDouble(),
                            home = odd.home_od?.toDouble(),
                            draw = odd.draw_od?.toDouble(),
                            away = odd.away_od?.toDouble()
                        )
                    )
                }
            }

        }

        return fifaGenericOddRequests
    }
}