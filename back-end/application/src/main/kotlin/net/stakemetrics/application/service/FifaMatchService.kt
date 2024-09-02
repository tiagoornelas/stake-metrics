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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FifaMatchService @Autowired constructor(
    private val logger: Logger,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaPlayerService: FifaPlayerService,
    private val fifaMatchRepository: IFifaMatchRepository,
    private val environmentVerifier: EnvironmentVerifier
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

    fun save(fifaMatch: FifaMatch) {
        fifaMatchRepository.save(fifaMatch)
    }

    fun buildAndSave(dto: FifaDataSourceDTO.FifaMatchRequest) {
        logger.log("Saving match ${dto.integrationId}")
        val league = fifaLeagueService.findByIntegrationId(dto.leagueId)

        val existingMatch = fifaMatchRepository.findByIntegrationId(dto.integrationId)

        val match = existingMatch?.copy(
            time = dto.time,
            status = dto.status,
            league = league,
            homeGoalsAtHalfTime = dto.homeGoalsAtHalfTime,
            homeGoalsAtFullTime = dto.homeGoalsAtFullTime,
            awayGoalsAtHalfTime = dto.awayGoalsAtHalfTime,
            awayGoalsAtFullTime = dto.awayGoalsAtFullTime,
            totalGoalsAtHalfTime = dto.totalGoalsAtHalfTime,
            totalGoalsAtFullTime = dto.totalGoalsAtFullTime,
            winner = determineWinner(dto.winner, dto.home, dto.away, league)
        ) ?: FifaMatch(
            integrationId = dto.integrationId,
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
            winner = determineWinner(dto.winner, dto.home, dto.away, league)
        )

        fifaMatchRepository.save(match)
    }

    fun findByIntegrationId(integrationId: Long): FifaMatch? {
        return fifaMatchRepository.findByIntegrationId(integrationId)
    }

    private fun findOrCreatePlayer(name: String, league: FifaLeague): FifaPlayer {
        return try {
            fifaPlayerService.findByName(name)
        } catch (e: NotFoundException) {
            val newPlayer = FifaPlayer(name = name, league = league)
            fifaPlayerService.save(newPlayer)
            newPlayer
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

}