package net.stakemetrics.application.workers

import java.util.Calendar
import java.util.Date
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.GenericScope
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.enums.MatchupTypes
import net.stakemetrics.application.entities.enums.StrategyScopeTypes
import net.stakemetrics.application.service.FifaMatchService
import org.springframework.stereotype.Service

@Service
class FifaPastResultsSearcher(private val fifaMatchService: FifaMatchService) {

    fun search(fifaMatch: FifaMatch): MutableSet<FifaStrategyDTO.GenericScopePastResults> {
        val results = mutableSetOf<FifaStrategyDTO.GenericScopePastResults>()

        val matchQuickIdentifier =
            FifaStrategyDTO.FifaMatchQuickIdentifier(fifaMatch.home, fifaMatch.away, fifaMatch.league)
        val allMatches = fetchAllRelevantMatches(matchQuickIdentifier)
        val organizedMatches = organizeMatchesByScopesAndMatchups(allMatches, matchQuickIdentifier)

        getAllPossibleScopes().forEach { scope ->
            val pastResults = organizedMatches[scope] ?: mutableSetOf()
            results.add(FifaStrategyDTO.GenericScopePastResults(scope, pastResults))
        }

        return results
    }

    private fun fetchAllRelevantMatches(match: FifaStrategyDTO.FifaMatchQuickIdentifier): MutableSet<FifaMatch> {
        val results = mutableSetOf<FifaMatch>()
        val scopeWithLongestTimeRange = StrategyScopeTypes.entries.maxByOrNull { it.daysValue }!!
        val sinceDate = getSinceDate(scopeWithLongestTimeRange)

        val homePlayerResults = fifaMatchService.listFinishedMatchesByPlayerSince(
            match.league!!, match.home!!,
            sinceDate
        )
        val awayPlayerResults = fifaMatchService.listFinishedMatchesByPlayerSince(
            match.league, match.away!!,
            sinceDate
        )

        results.addAll(homePlayerResults)
        results.addAll(awayPlayerResults)

        return results
    }

    private fun organizeMatchesByScopesAndMatchups(
        matches: MutableSet<FifaMatch>,
        match: FifaStrategyDTO.FifaMatchQuickIdentifier
    ): Map<GenericScope, MutableSet<FifaMatch>> {
        val organizedMatches = mutableMapOf<GenericScope, MutableSet<FifaMatch>>()

        for (matchup in MatchupTypes.entries) {
            for (scopeType in StrategyScopeTypes.entries) {
                val scope = GenericScope(matchup = matchup, type = scopeType)
                val sinceDate = getSinceDate(scopeType)
                val filteredMatches =
                    matches.filter { it.time.after(sinceDate) && isValidMatchup(it, matchup, match) }.toMutableSet()
                organizedMatches[scope] = filteredMatches
            }
        }

        return organizedMatches
    }

    private fun isValidMatchup(
        match: FifaMatch,
        matchupType: MatchupTypes,
        matchQuickIdentifier: FifaStrategyDTO.FifaMatchQuickIdentifier
    ): Boolean {
        return when (matchupType) {
            MatchupTypes.VS_ANYONE -> true
            MatchupTypes.VS_EACH_OTHER -> (match.home == matchQuickIdentifier.home && match.away == matchQuickIdentifier.away) ||
                    (match.home == matchQuickIdentifier.away && match.away == matchQuickIdentifier.home)
        }
    }

    private fun getAllPossibleScopes(): Set<GenericScope> {
        val allScopes = mutableSetOf<GenericScope>()

        for (matchup in MatchupTypes.entries) {
            for (scopeType in StrategyScopeTypes.entries) {
                val scope = GenericScope(
                    matchup = matchup,
                    type = scopeType
                )
                allScopes.add(scope)
            }
        }

        return allScopes
    }

    private fun getSinceDate(type: StrategyScopeTypes): Date {
        return Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -type.daysValue) }.time
    }
}