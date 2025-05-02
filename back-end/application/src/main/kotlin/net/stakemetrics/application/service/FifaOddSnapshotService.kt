package net.stakemetrics.application.service

import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.FifaTrendScopeAnalysis
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.dtos.TrendDTO
import net.stakemetrics.application.entities.enums.DateIntervalTypes
import net.stakemetrics.application.repositories.IFifaOddSnapshotRepository
import net.stakemetrics.application.utils.DateHelper
import net.stakemetrics.application.utils.MathHelper
import net.stakemetrics.application.workers.FIfaIntegrationHomeAndAwayMismatchFinder
import net.stakemetrics.application.workers.FifaOddSnapshotCloser
import net.stakemetrics.application.workers.FifaPastResultsSearcher
import net.stakemetrics.application.workers.OddAndLineCalculator
import net.stakemetrics.application.workers.enqueuers.FifaStrategyAgainstOddsEnqueuer
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FifaOddSnapshotService(
    private val mathHelper: MathHelper,
    private val dateHelper: DateHelper,
    private val leagueService: FifaLeagueService,
    private val fifaMatchService: FifaMatchService,
    private val oddAndLineCalculator: OddAndLineCalculator,
    private val fifaOddSnapshotCloser: FifaOddSnapshotCloser,
    private val fifaPastResultsSearcher: FifaPastResultsSearcher,
    private val fifaOddSnapshotRepository: IFifaOddSnapshotRepository,
    private val fifaStrategyAgainstOddsEnqueuer: FifaStrategyAgainstOddsEnqueuer,
    private val fIfaIntegrationHomeAndAwayMismatchFinder: FIfaIntegrationHomeAndAwayMismatchFinder
) {

    fun save(oddSnapshot: FifaOddSnapshot) {
        fifaOddSnapshotRepository.save(oddSnapshot)
    }

    fun getById(id: UUID): FifaOddSnapshot {
        return fifaOddSnapshotRepository.findById(id)
    }

    fun getByMatchId(matchId: UUID): List<FifaOddSnapshot> {
        return fifaOddSnapshotRepository.findAllByFifaMatchId(matchId)
    }

    fun closeOddSnapshot(oddSnapshot: FifaOddSnapshot) {
        fifaOddSnapshotCloser.close(oddSnapshot)
    }

    fun runTrendAnalysis(payload: FifaDataSourceDTO.FifaOddRequest) {
        val fifaMatch = fifaMatchService.getOrCreateMatchByOdd(payload)

        if (fifaMatch.hasAlreadyStarted()) return

        val matchHomeAndAwayWasSwappedByIntegration =
            fIfaIntegrationHomeAndAwayMismatchFinder.checkForHomeAndAwaySwappedByIntegration(fifaMatch, payload)

        if (matchHomeAndAwayWasSwappedByIntegration) {
            val fixedMatch = fifaMatchService.checkAndFixHomeAndAwaySwappedByIntegration(fifaMatch, payload)
            cleanOddSnapshotsForMatch(fixedMatch)
            createOddSnapshot(fixedMatch, payload.odds)
        } else {
            val oddsAlreadyAnalyzed = checkIfOddsWereAnalyzed(fifaMatch, payload.odds)
            if (!oddsAlreadyAnalyzed) createOddSnapshot(fifaMatch, payload.odds)
        }
    }

    private fun checkIfOddsWereAnalyzed(
        fifaMatch: FifaMatch,
        incomingOdd: FifaDataSourceDTO.FifaGenericOddRequest
    ): Boolean {
        val existingSnapshots = getByMatchId(fifaMatch.id)
        return existingSnapshots.any { snapshot ->
            snapshot.goalsHandicap == incomingOdd.goalsHandicap &&
                    snapshot.overGoalsOdd == incomingOdd.overGoals &&
                    snapshot.underGoalsOdd == incomingOdd.underGoals &&
                    snapshot.homeOdd == incomingOdd.home &&
                    snapshot.drawOdd == incomingOdd.draw &&
                    snapshot.awayOdd == incomingOdd.away
        }
    }

    private fun cleanOddSnapshotsForMatch(fifaMatch: FifaMatch) {
        fifaOddSnapshotRepository.deleteAllByFifaMatchId(fifaMatch.id)
    }

    private fun createOddSnapshot(
        fifaMatch: FifaMatch,
        newOdd: FifaDataSourceDTO.FifaGenericOddRequest
    ) {
        val oldOdds = fifaOddSnapshotRepository.findAllByFifaMatchId(fifaMatch.id).toMutableSet()
        val snapshotWithSimilarLine = getSimilarHandicapSnapshot(newOdd, oldOdds)
        if (snapshotWithSimilarLine == null) {
            createNewSnapshot(fifaMatch, newOdd)
        } else {
            updateOdds(fifaMatch, newOdd, snapshotWithSimilarLine)
        }
    }

    private fun getSimilarHandicapSnapshot(
        newOdd: FifaDataSourceDTO.FifaGenericOddRequest,
        oldOdds: MutableSet<FifaOddSnapshot>
    ): FifaOddSnapshot? {
        return oldOdds.firstOrNull { oldOdd ->
            oldOdd.goalsHandicap == newOdd.goalsHandicap &&
                    oldOdd.overGoalsOdd == newOdd.overGoals &&
                    oldOdd.underGoalsOdd == newOdd.underGoals &&
                    oldOdd.homeOdd == newOdd.home &&
                    oldOdd.drawOdd == newOdd.draw &&
                    oldOdd.awayOdd == newOdd.away
        }
    }

    private fun createNewSnapshot(fifaMatch: FifaMatch, newOdd: FifaDataSourceDTO.FifaGenericOddRequest) {
        val allScopesWithResults = fifaPastResultsSearcher.search(fifaMatch)
        val scopesWithAnalysis = mutableSetOf<FifaTrendScopeAnalysis>()

        allScopesWithResults.forEach { (scope, results) ->
            val goalLineThreshold = oddAndLineCalculator.getScoreThreshold(newOdd.goalsHandicap!!)
            val (homePlayerProbability, drawProbability, awayPlayerProbability)
                    = getMatchOddsProbabilities(fifaMatch, results)
            val (overProbability, underProbability) = getGoalLineProbabilities(goalLineThreshold, results)

            val homePlayerFairLine = oddAndLineCalculator.getFairLine(homePlayerProbability)
            val drawFairLine = oddAndLineCalculator.getFairLine(drawProbability)
            val awayPlayerFairLine = oddAndLineCalculator.getFairLine(awayPlayerProbability)

            val homePlayerJuice = oddAndLineCalculator.getBettorsJuice(newOdd.home!!, homePlayerFairLine)
            val drawJuice = oddAndLineCalculator.getBettorsJuice(newOdd.draw!!, drawFairLine)
            val awayPlayerJuice = oddAndLineCalculator.getBettorsJuice(newOdd.away!!, awayPlayerFairLine)

            val overFairLine = oddAndLineCalculator.getFairLine(overProbability)
            val underFairLine = oddAndLineCalculator.getFairLine(underProbability)

            val overJuice = oddAndLineCalculator.getBettorsJuice(newOdd.overGoals!!, overFairLine)
            val underJuice = oddAndLineCalculator.getBettorsJuice(newOdd.underGoals!!, underFairLine)

            val scopeWithAnalysis = FifaTrendScopeAnalysis(
                matchup = scope?.matchup!!,
                type = scope.type,
                totalMatches = results.size,
                homePlayerProbability = homePlayerProbability,
                homePlayerFairLine = homePlayerFairLine,
                homePlayerJuice = homePlayerJuice,
                drawProbability = drawProbability,
                drawFairLine = drawFairLine,
                drawJuice = drawJuice,
                awayPlayerProbability = awayPlayerProbability,
                awayPlayerFairLine = awayPlayerFairLine,
                awayPlayerJuice = awayPlayerJuice,
                overProbability = overProbability,
                overFairLine = overFairLine,
                overJuice = overJuice,
                underProbability = underProbability,
                underFairLine = underFairLine,
                underJuice = underJuice
            )

            scopesWithAnalysis.add(scopeWithAnalysis)
        }

        val fifaOddSnapshot = FifaOddSnapshot(
            fifaMatch = fifaMatch,
            goalsHandicap = newOdd.goalsHandicap,
            overGoalsOdd = newOdd.overGoals,
            underGoalsOdd = newOdd.underGoals,
            homeOdd = newOdd.home,
            drawOdd = newOdd.draw,
            awayOdd = newOdd.away,
            trendScopeAnalysis = scopesWithAnalysis
        )

        fifaOddSnapshot.validate()
        save(fifaOddSnapshot)
        fifaStrategyAgainstOddsEnqueuer.enqueue(fifaOddSnapshot)
    }

    private fun updateOdds(
        fifaMatch: FifaMatch,
        newOdd: FifaDataSourceDTO.FifaGenericOddRequest,
        similarSnapshot: FifaOddSnapshot
    ) {
        val fifaOddSnapshot = FifaOddSnapshot(
            fifaMatch = fifaMatch,
            goalsHandicap = newOdd.goalsHandicap,
            overGoalsOdd = newOdd.overGoals,
            underGoalsOdd = newOdd.underGoals,
            homeOdd = newOdd.home,
            drawOdd = newOdd.draw,
            awayOdd = newOdd.away,
            trendScopeAnalysis = similarSnapshot.trendScopeAnalysis
        )

        fifaOddSnapshot.validate()
        save(fifaOddSnapshot)
        fifaStrategyAgainstOddsEnqueuer.enqueue(fifaOddSnapshot)
    }

    private fun getMatchOddsProbabilities(
        fifaMatch: FifaMatch, results: MutableSet<FifaMatch>
    ): Triple<Double, Double, Double> {
        val homePlayer = fifaMatch.home
        val awayPlayer = fifaMatch.away

        val homePlayerMatches = results.filter { it.home == homePlayer || it.away == homePlayer }.toMutableSet()
        val awayPlayerMatches = results.filter { it.home == awayPlayer || it.away == awayPlayer }.toMutableSet()

        val drawMatchCount = getDrawMatchCount(results)
        val homePlayerWonMatchesCount = getPlayerWonMatchesCount(homePlayerMatches, homePlayer!!)
        val awayPlayerWonMatchesCount = getPlayerWonMatchesCount(awayPlayerMatches, awayPlayer!!)

        val drawProbability = mathHelper.safeDivide(drawMatchCount, results.size)
        val homeWinProbability = mathHelper.safeDivide(homePlayerWonMatchesCount, homePlayerMatches.size)
        val awayWinProbability = mathHelper.safeDivide(awayPlayerWonMatchesCount, awayPlayerMatches.size)

        return Triple(homeWinProbability, drawProbability, awayWinProbability)
    }

    private fun getGoalLineProbabilities(threshold: Double, results: MutableSet<FifaMatch>): Pair<Double, Double> {
        val totalMatchesInScope: Int = getMatchCount(results)
        val voidMatches: Int = getVoidMatchCount(results, threshold)
        val matchesOverThreshold: Int = getMatchesOverThreshold(results, threshold)
        val matchesUnderThreshold: Int = getMatchesUnderThreshold(results, threshold)

        val accountableTotalMatches = totalMatchesInScope - voidMatches
        val scopeOverProbability = mathHelper.safeDivide(matchesOverThreshold, accountableTotalMatches)
        val scopeUnderProbability = mathHelper.safeDivide(matchesUnderThreshold, accountableTotalMatches)

        return Pair(scopeOverProbability, scopeUnderProbability)
    }

    private fun getDrawMatchCount(results: MutableSet<FifaMatch>): Int {
        return results.count { it.winner == null }
    }

    private fun getPlayerWonMatchesCount(results: MutableSet<FifaMatch>, player: FifaPlayer): Int {
        return results.count { it.winner == player }
    }

    private fun getMatchCount(results: MutableSet<FifaMatch>): Int {
        return results.count { it.totalGoalsAtFullTime != null }
    }

    private fun getVoidMatchCount(results: MutableSet<FifaMatch>, threshold: Double): Int {
        return if (threshold % 1.0 == 0.0) {
            results.count { it.totalGoalsAtFullTime != null && it.totalGoalsAtFullTime == threshold.toInt() }
        } else {
            0
        }
    }

    private fun getMatchesOverThreshold(results: MutableSet<FifaMatch>, threshold: Double): Int {
        return results.count { it.totalGoalsAtFullTime != null && it.totalGoalsAtFullTime > threshold }
    }

    private fun getMatchesUnderThreshold(results: MutableSet<FifaMatch>, threshold: Double): Int {
        return results.count { it.totalGoalsAtFullTime != null && it.totalGoalsAtFullTime < threshold }
    }

    fun getAllOddSnapshotsForDate(date: String): List<FifaOddSnapshot> {
        val (dateStart, dateEnd) = dateHelper.getDateRangeByString(date)
        return fifaOddSnapshotRepository.findAllClosedByCreatedAtBetween(dateStart, dateEnd)
    }

    fun getAllLeaguesGoalsTrend(dateInterval: DateIntervalTypes): List<TrendDTO.FifaGoalsLeagueTrendResponse> {
        val daysOffset = dateInterval.daysValue
        val leagues = leagueService.listActiveLeagues()

        val leagueTrends = mutableListOf<TrendDTO.FifaGoalsLeagueTrendResponse>()
        leagues.forEach { league ->
            val leagueTrend = getLeagueGoalsTrend(daysOffset, league)
            leagueTrends.add(TrendDTO.FifaGoalsLeagueTrendResponse(league, leagueTrend))
        }
        return leagueTrends
    }

    fun getLeagueGoalsTrend(daysOffset: Int, league: FifaLeague): List<Int> {
        return fifaOddSnapshotRepository.getLeagueGoalsTrendForLeagueAtInterval(league, daysOffset)
    }

}