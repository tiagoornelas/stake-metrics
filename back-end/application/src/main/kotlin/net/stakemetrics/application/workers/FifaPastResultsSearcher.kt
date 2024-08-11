package net.stakemetrics.application.workers

import java.util.Calendar
import java.util.Date
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.enums.FifaMatchupTypes
import net.stakemetrics.application.entities.enums.FifaStrategyScopeTypes
import net.stakemetrics.application.entities.exceptions.FifaMatchIntegrationDataException
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.service.FifaLeagueService
import net.stakemetrics.application.service.FifaMatchService
import net.stakemetrics.application.service.FifaPlayerService
import net.stakemetrics.application.utils.Logger
import org.springframework.stereotype.Service

@Service
class FifaPastResultsSearcher(
    private val fifaMatchService: FifaMatchService,
    private val fifaPlayerService: FifaPlayerService,
    private val fifaLeagueService: FifaLeagueService,
    private val logger: Logger
) {

    fun search(request: FifaStrategyDTO.FifaStrategyAgainstOddRequest): MutableSet<FifaStrategyDTO.FifaStrategyScopePastResults> {
        val results = mutableSetOf<FifaStrategyDTO.FifaStrategyScopePastResults>()
        val (strategy, odds) = request

        val homePlayer: FifaPlayer?
        val awayPlayer: FifaPlayer?
        val league: FifaLeague?

        try {
            homePlayer = fifaPlayerService.findByName(odds.homePlayerName)
            awayPlayer = fifaPlayerService.findByName(odds.awayPlayerName)
            league = fifaLeagueService.findByIntegrationId(odds.leagueIntegrationId)
        } catch (e: NotFoundException) {
            logger.log("The past results searcher could not find player or league in the database. Cause: ${e.message}")
            return results
        }

        val matchContainsExcludedPlayers =
            strategy.excludedPlayers.contains(homePlayer) || strategy.excludedPlayers.contains(awayPlayer)
        if (matchContainsExcludedPlayers) return results

        val matchQuickIdentifier = FifaStrategyDTO.FifaMatchQuickIdentifier(homePlayer, awayPlayer, league)

        strategy.scopes.forEach { scope ->
            if (scope.matchup == null || scope.type == null || scope.value == 0)
                throw IllegalArgumentException("Scope is missing required fields when trying to get past results.")

            val pastResults = searchScope(scope.matchup, scope.type, scope.value, matchQuickIdentifier)
            results.add(FifaStrategyDTO.FifaStrategyScopePastResults(scope, pastResults))
        }

        return results
    }

    private fun searchScope(
        matchup: FifaMatchupTypes,
        type: FifaStrategyScopeTypes,
        value: Int,
        match: FifaStrategyDTO.FifaMatchQuickIdentifier
    ): MutableSet<FifaMatch> {
        return when (type) {
            FifaStrategyScopeTypes.HOURS, FifaStrategyScopeTypes.DAYS -> {
                val isSameMatchup = matchup == FifaMatchupTypes.VS_EACH_OTHER
                searchMatchupSinceDate(getSinceDate(value, type), match, isSameMatchup)
            }

            FifaStrategyScopeTypes.MATCHES -> {
                val isSameMatchup = matchup == FifaMatchupTypes.VS_EACH_OTHER
                searchMatchupLastMatches(value, match, isSameMatchup)
            }
        }
    }

    private fun searchMatchupSinceDate(
        sinceDate: Date,
        match: FifaStrategyDTO.FifaMatchQuickIdentifier,
        isSameMatchup: Boolean
    ): MutableSet<FifaMatch> {
        if (match.league == null || match.home == null || match.away == null)
            throw FifaMatchIntegrationDataException(
                "Match sent by odd provider is missing league, home or away " +
                        "player."
            )

        val results = mutableSetOf<FifaMatch>()

        if (isSameMatchup) {
            val firstLegResults =
                fifaMatchService.listFinishedMatchesByMatchupSince(
                    match.league,
                    match.home,
                    match.away,
                    sinceDate
                )
            results.addAll(firstLegResults)

            val secondLegResults =
                fifaMatchService.listFinishedMatchesByMatchupSince(
                    match.league,
                    match.away,
                    match.home,
                    sinceDate
                )
            results.addAll(secondLegResults)
        } else {
            val homePlayerResults =
                fifaMatchService.listFinishedMatchesByPlayerSince(
                    match.league,
                    match.home,
                    sinceDate
                )
            results.addAll(homePlayerResults)

            val awayPlayerResults =
                fifaMatchService.listFinishedMatchesByPlayerSince(
                    match.league,
                    match.away,
                    sinceDate
                )
            results.addAll(awayPlayerResults)
        }

        return results
    }

    private fun searchMatchupLastMatches(
        last: Int,
        match: FifaStrategyDTO.FifaMatchQuickIdentifier,
        isSameMatchup: Boolean
    ): MutableSet<FifaMatch> {
        if (match.league == null || match.home == null || match.away == null)
            throw FifaMatchIntegrationDataException(
                "Match sent by odd provider is missing league, home or away " +
                        "player."
            )

        val results = mutableSetOf<FifaMatch>()

        if (isSameMatchup) {
            val firstLegResults =
                fifaMatchService.listLastFinishedMatchesByMatchup(match.league, match.home, match.away, last)
            results.addAll(firstLegResults)

            val secondLegResults =
                fifaMatchService.listLastFinishedMatchesByMatchup(match.league, match.away, match.home, last)
            results.addAll(secondLegResults)
        } else {
            val homePlayerResults =
                fifaMatchService.listLastFinishedMatchesByPlayer(match.league, match.home, last)
            results.addAll(homePlayerResults)

            val awayPlayerResults =
                fifaMatchService.listLastFinishedMatchesByPlayer(match.league, match.away, last)
            results.addAll(awayPlayerResults)
        }

        return results
    }

    private fun getSinceDate(value: Int, type: FifaStrategyScopeTypes): Date {
        return when (type) {
            FifaStrategyScopeTypes.HOURS -> Calendar.getInstance().apply { add(Calendar.HOUR, -value) }.time
            FifaStrategyScopeTypes.DAYS -> Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -value) }.time
            FifaStrategyScopeTypes.MATCHES -> throw IllegalArgumentException("Matchup type MATCHES is not supported for since date.")
        }
    }
}