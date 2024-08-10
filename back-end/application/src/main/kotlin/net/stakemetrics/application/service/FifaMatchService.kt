package net.stakemetrics.application.service

import java.util.Calendar
import java.util.Date
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IFifaMatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FifaMatchService @Autowired constructor(
    private val fifaLeagueService: FifaLeagueService,
    private val fifaPlayerService: FifaPlayerService,
    private val fifaMatchRepository: IFifaMatchRepository,
) {

    fun getLastMatchResultTime(): Long {
        val latestMatch = fifaMatchRepository.findLatestMatch()
        if (latestMatch != null) {
            return latestMatch.time.time / 1000
        } else {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -60)
            val aWeekAgo = calendar.time
            return aWeekAgo.time / 1000
        }
    }

    fun saveMatch(dto: FifaDTO.FifaMatchRequest) {
        val league = fifaLeagueService.findByIntegrationId(dto.leagueId)
        val matchDate = Date(dto.time.toLong() * 1000)
        val matchStatus = FifaMatchStatusTypes.entries.find { it.ordinal == dto.status }
            ?: throw IllegalArgumentException("Invalid match status")

        val existingMatch = fifaMatchRepository.findByIntegrationId(dto.integrationId)

        val match = existingMatch?.copy(
            time = matchDate,
            status = matchStatus,
            league = league,
            homeGoalsAtHalfTime = dto.homeGoalsAtHalfTime,
            homeGoalsAtFullTime = dto.homeGoalsAtFullTime,
            awayGoalsAtHalfTime = dto.awayGoalsAtHalfTime,
            awayGoalsAtFullTime = dto.awayGoalsAtFullTime,
            totalGoalsAtHalfTime = dto.totalGoalsAtHalfTime,
            totalGoalsAtFullTime = dto.totalGoalsAtFullTime
        ) ?: FifaMatch(
            integrationId = dto.integrationId,
            time = matchDate,
            status = matchStatus,
            league = league,
            home = findOrCreatePlayer(dto.home, league),
            away = findOrCreatePlayer(dto.away, league),
            homeGoalsAtHalfTime = dto.homeGoalsAtHalfTime,
            homeGoalsAtFullTime = dto.homeGoalsAtFullTime,
            awayGoalsAtHalfTime = dto.awayGoalsAtHalfTime,
            awayGoalsAtFullTime = dto.awayGoalsAtFullTime,
            totalGoalsAtHalfTime = dto.totalGoalsAtHalfTime,
            totalGoalsAtFullTime = dto.totalGoalsAtFullTime,
            winner = determineAndSaveWinner(dto.winner, dto.home, dto.away, league)
        )

        fifaMatchRepository.save(match)
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

    private fun determineAndSaveWinner(winner: String?, home: String, away: String, league: FifaLeague): FifaPlayer? {
        return when (winner) {
            home -> findOrCreatePlayer(home, league)
            away -> findOrCreatePlayer(away, league)
            else -> null
        }
    }

    fun listFinishedMatchesByPlayerSince(league: FifaLeague, player: FifaPlayer, since: Date): List<FifaMatch> {
        return fifaMatchRepository.listFinishedMatchesByPlayerSince(league, player, since)
    }

    fun listLastFinishedMatchesByPlayer(league: FifaLeague, player: FifaPlayer, last: Int): List<FifaMatch> {
        return fifaMatchRepository.listLastFinishedMatchesByPlayer(league, player, last)
    }

    fun listFinishedMatchesByMatchupSince(
        league: FifaLeague, homePlayer: FifaPlayer, awayPlayer: FifaPlayer, since:
        Date
    ): List<FifaMatch> {
        return fifaMatchRepository.listFinishedMatchesByMatchupSince(league, homePlayer, awayPlayer, since)
    }

    fun listLastFinishedMatchesByMatchup(
        league: FifaLeague, homePlayer: FifaPlayer, awayPlayer: FifaPlayer, last:
        Int
    ): List<FifaMatch> {
        return fifaMatchRepository.listLastFinishedMatchesByMatchup(league, homePlayer, awayPlayer, last)
    }

}