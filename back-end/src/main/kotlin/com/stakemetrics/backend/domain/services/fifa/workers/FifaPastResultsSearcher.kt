package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.domain.entities.fifa.FifaLeague
import com.stakemetrics.backend.domain.entities.fifa.FifaMatch
import com.stakemetrics.backend.domain.entities.fifa.FifaPlayer
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyScope
import com.stakemetrics.backend.domain.enums.fifa.FifaMatchupTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyScopeTypes
import com.stakemetrics.backend.domain.exceptions.FifaMatchIntegrationDataException
import com.stakemetrics.backend.domain.exceptions.FifaStrategyRuleBreakException
import com.stakemetrics.backend.domain.exceptions.NotFoundException
import com.stakemetrics.backend.domain.ports.fifa.FifaLeagueRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaMatchRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaPlayerRepositoryPort
import java.util.Calendar
import java.util.Date
import org.springframework.stereotype.Service

data class FifaStrategyScopePastResults(
    val scope: FifaStrategyScope? = null,
    val pastResults: MutableSet<FifaMatch> = mutableSetOf()
)

data class FifaMatchQuickIdentifier(
    val home: FifaPlayer? = null,
    val away: FifaPlayer? = null,
    val league: FifaLeague? = null
)

@Service
class FifaPastResultsSearcher(
    private val fifaMatchRepositoryPort: FifaMatchRepositoryPort,
    private val fifaPlayerRepositoryPort: FifaPlayerRepositoryPort,
    private val fifaLeagueRepositoryPort: FifaLeagueRepositoryPort
) {


    fun search(
        leagueIntegrationId: Int,
        homePlayerName: String,
        awayPlayerName: String,
        leagues: Set<FifaLeague>,
        scopes: Set<FifaStrategyScope>
    ): MutableSet<FifaStrategyScopePastResults> {
        val results = mutableSetOf<FifaStrategyScopePastResults>()

        val homePlayer = fifaPlayerRepositoryPort.findByName(homePlayerName)
            ?: throw NotFoundException("Player", "name", homePlayerName)
        val awayPlayer = fifaPlayerRepositoryPort.findByName(awayPlayerName)
            ?: throw NotFoundException("Player", "name", awayPlayerName)
        val league = fifaLeagueRepositoryPort.findByIntegrationId(leagueIntegrationId)
            ?: throw NotFoundException("League", "integrationId", leagueIntegrationId.toString())

        if (!leagues.contains(league)) throw FifaStrategyRuleBreakException(
            "Odd sent is for a league that the strategy does not observe."
        )

        val matchQuickIdentifier = FifaMatchQuickIdentifier(homePlayer, awayPlayer, league)

        scopes.forEach { scope ->
            if (scope.matchup == null || scope.type == null || scope.value == 0)
                throw IllegalArgumentException("Scope is missing required fields when trying to get past results.")

            val pastResults = searchScope(scope.matchup, scope.type, scope.value, matchQuickIdentifier)
            results.add(FifaStrategyScopePastResults(scope, pastResults))
        }

        return results
    }

    private fun searchScope(
        matchup: FifaMatchupTypes,
        type: FifaStrategyScopeTypes,
        value: Int,
        match: FifaMatchQuickIdentifier
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
        match: FifaMatchQuickIdentifier,
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
                fifaMatchRepositoryPort.listFinishedMatchesByMatchupSince(
                    match.league,
                    match.home,
                    match.away,
                    sinceDate
                )
            results.addAll(firstLegResults)

            val secondLegResults =
                fifaMatchRepositoryPort.listFinishedMatchesByMatchupSince(
                    match.league,
                    match.away,
                    match.home,
                    sinceDate
                )
            results.addAll(secondLegResults)
        } else {
            val homePlayerResults =
                fifaMatchRepositoryPort.listFinishedMatchesByPlayerSince(
                    match.league,
                    match.home,
                    sinceDate
                )
            results.addAll(homePlayerResults)

            val awayPlayerResults =
                fifaMatchRepositoryPort.listFinishedMatchesByPlayerSince(
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
        match: FifaMatchQuickIdentifier,
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
                fifaMatchRepositoryPort.listLastFinishedMatchesByMatchup(match.league, match.home, match.away, last)
            results.addAll(firstLegResults)

            val secondLegResults =
                fifaMatchRepositoryPort.listLastFinishedMatchesByMatchup(match.league, match.away, match.home, last)
            results.addAll(secondLegResults)
        } else {
            val homePlayerResults =
                fifaMatchRepositoryPort.listLastFinishedMatchesByPlayer(match.league, match.home, last)
            results.addAll(homePlayerResults)

            val awayPlayerResults =
                fifaMatchRepositoryPort.listLastFinishedMatchesByPlayer(match.league, match.away, last)
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