package net.stakemetrics.application.service

import java.util.Calendar
import java.util.Date
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.annotations.EnvironmentSensitive
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IFifaMatchRepository
import net.stakemetrics.application.utils.EnvironmentVerifier
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.application.workers.FIfaIntegrationHomeAndAwayMismatchFinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class FifaMatchService @Autowired constructor(
    private val logger: Logger,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaPlayerService: FifaPlayerService,
    private val environmentVerifier: EnvironmentVerifier,
    private val fifaMatchRepository: IFifaMatchRepository,
    private val fIfaIntegrationHomeAndAwayMismatchFinder: FIfaIntegrationHomeAndAwayMismatchFinder
) {
    @EnvironmentSensitive
    fun getLastMatchResultTime(): Date {
        val latestMatch = fifaMatchRepository.findLatestMatch()
        return if (latestMatch != null) {
            latestMatch.time
        } else {
            val populateDatabaseDays = if (environmentVerifier.isProd()) 60 else 1
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -populateDatabaseDays)
            calendar.time
        }
    }

    @EnvironmentSensitive
    fun getLastMatchResultTimeForLeague(league: FifaLeague): Date {
        val latestMatch = fifaMatchRepository.findLatestMatchForLeague(league)
        return if (latestMatch != null) {
            latestMatch.time
        } else {
            val populateDatabaseDays = if (environmentVerifier.isProd()) 60 else 1
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -populateDatabaseDays)
            calendar.time
        }
    }

    fun save(fifaMatch: FifaMatch) {
        fifaMatchRepository.save(fifaMatch)
    }

    fun buildAndSave(dto: FifaDataSourceDTO.FifaMatchRequest) {
        val league = fifaLeagueService.findByIntegrationId(dto.leagueId)
        val existingMatch = fifaMatchRepository.findByIntegrationId(dto.integrationId)

        val match: FifaMatch = if (existingMatch != null) {
            val matchHomeAndAwayWasSwappedByIntegration =
                fIfaIntegrationHomeAndAwayMismatchFinder.checkForHomeAndAwaySwappedByIntegration(existingMatch, dto)

            if (matchHomeAndAwayWasSwappedByIntegration) {
                logger.log("Match result mismatch found: ${existingMatch.home?.name} vs ${existingMatch.away?.name} for match ${existingMatch.integrationId}")
            }

            val (homeGoalsAtHalfTime, awayGoalsAtHalfTime) = swapValuesIfNecessary(
                matchHomeAndAwayWasSwappedByIntegration,
                dto.homeGoalsAtHalfTime,
                dto.awayGoalsAtHalfTime
            )
            val (homeGoalsAtFullTime, awayGoalsAtFullTime) = swapValuesIfNecessary(
                matchHomeAndAwayWasSwappedByIntegration,
                dto.homeGoalsAtFullTime,
                dto.awayGoalsAtFullTime
            )


            existingMatch.copy(
                time = dto.time,
                status = dto.status,
                league = league,
                bet365Id = dto.bet365Id ?: existingMatch.bet365Id,
                homeGoalsAtHalfTime = homeGoalsAtHalfTime,
                awayGoalsAtHalfTime = awayGoalsAtHalfTime,
                homeGoalsAtFullTime = homeGoalsAtFullTime,
                awayGoalsAtFullTime = awayGoalsAtFullTime,
                totalGoalsAtHalfTime = dto.totalGoalsAtHalfTime,
                totalGoalsAtFullTime = dto.totalGoalsAtFullTime,
                winner = getWinnerBasedOnSwap(dto, matchHomeAndAwayWasSwappedByIntegration, league)
            )
        } else {
            FifaMatch(
                integrationId = dto.integrationId,
                bet365Id = dto.bet365Id,
                time = dto.time,
                status = dto.status,
                league = league,
                home = findOrCreatePlayer(dto.home, league),
                away = findOrCreatePlayer(dto.away, league),
                homeGoalsAtHalfTime = dto.homeGoalsAtHalfTime,
                homeGoalsAtFullTime = dto.homeGoalsAtFullTime,
                awayGoalsAtHalfTime = dto.awayGoalsAtHalfTime,
                awayGoalsAtFullTime = dto.awayGoalsAtFullTime,
                totalGoalsAtHalfTime = dto.totalGoalsAtHalfTime,
                totalGoalsAtFullTime = dto.totalGoalsAtFullTime,
                winner = getWinnerBasedOnSwap(dto, false, league)
            )
        }

        fifaMatchRepository.save(match)
    }

    private fun swapValuesIfNecessary(isSwapped: Boolean, homeValue: Int?, awayValue: Int?): Pair<Int?, Int?> {
        return if (isSwapped) {
            awayValue to homeValue
        } else {
            homeValue to awayValue
        }
    }

    private fun getWinnerBasedOnSwap(
        dto: FifaDataSourceDTO.FifaMatchRequest,
        isSwapped: Boolean,
        league: FifaLeague
    ): FifaPlayer? {
        return if (isSwapped) {
            determineWinner(dto.winner, dto.away, dto.home, league)
        } else {
            determineWinner(dto.winner, dto.home, dto.away, league)
        }
    }

    fun findByIntegrationId(integrationId: Long): FifaMatch? {
        return fifaMatchRepository.findByIntegrationId(integrationId)
    }

    private fun findOrCreatePlayer(name: String, league: FifaLeague): FifaPlayer {
        return try {
            fifaPlayerService.findByName(name)
        } catch (e: NotFoundException) {
            try {
                val newPlayer = FifaPlayer(name = name, league = league)
                fifaPlayerService.save(newPlayer)
                newPlayer
            } catch (e: DataIntegrityViolationException) {
                fifaPlayerService.findByName(name)
            }
        }
    }

    private fun determineWinner(winner: String?, home: String, away: String, league: FifaLeague): FifaPlayer? {
        return when (winner) {
            home -> findOrCreatePlayer(home, league)
            away -> findOrCreatePlayer(away, league)
            else -> null
        }
    }

    fun listFinishedMatchesByPlayerSince(league: FifaLeague, player: FifaPlayer, since: Date): List<FifaMatch> {
        return fifaMatchRepository.listFinishedMatchesByPlayerSince(league, player, since)
    }

    fun getOrCreateMatchByOdd(odd: FifaDataSourceDTO.FifaOddRequest): FifaMatch {
        val fifaLeague = fifaLeagueService.findByIntegrationId(odd.leagueIntegrationId)
        val home = fifaPlayerService.findByName(odd.homePlayerName)
        val away = fifaPlayerService.findByName(odd.awayPlayerName)

        val fifaMatch = fifaMatchRepository.findByIntegrationId(odd.matchIntegrationId) ?: run {
            val newFifaMatch = FifaMatch(
                integrationId = odd.matchIntegrationId,
                bet365Id = odd.bet365Id,
                time = odd.odds.matchTime!!,
                league = fifaLeague,
                home = home,
                away = away
            )
            try {
                fifaMatchRepository.save(newFifaMatch)
                newFifaMatch
            } catch (e: Exception) {
                logger.logError(e)
                fifaMatchRepository.findByIntegrationId(odd.matchIntegrationId)
            }
        }

        return fifaMatch!!
    }

    fun checkAndFixHomeAndAwaySwappedByIntegration(
        fifaMatch: FifaMatch,
        odd: FifaDataSourceDTO.FifaOddRequest
    ): FifaMatch {
        val editedMatch = fifaMatch.copy(home = fifaMatch.away, away = fifaMatch.home)
        fifaMatchRepository.save(editedMatch)
        return editedMatch
    }
}