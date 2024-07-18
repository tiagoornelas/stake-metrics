package com.stakemetrics.backend.domain.services

import com.stakemetrics.backend.domain.entities.FifaLeague
import com.stakemetrics.backend.domain.entities.FifaMatch
import com.stakemetrics.backend.domain.entities.FifaPlayer
import com.stakemetrics.backend.domain.enums.FifaMatchStatusTypes
import com.stakemetrics.backend.domain.exceptions.NotFoundException
import com.stakemetrics.backend.domain.ports.FifaLeagueRepositoryPort
import com.stakemetrics.backend.domain.ports.FifaMatchRepositoryPort
import com.stakemetrics.backend.domain.ports.FifaPlayerRepositoryPort
import com.stakemetrics.backend.plugins.http.ports.FifaServicePort
import java.util.Calendar
import java.util.Date

class FifaService(
    private val fifaLeagueRepositoryPort: FifaLeagueRepositoryPort,
    private val fifaMatchRepositoryPort: FifaMatchRepositoryPort,
    private val fifaPlayerRepositoryPort: FifaPlayerRepositoryPort
) : FifaServicePort {
    override fun listActiveLeagues(): List<FifaLeague> {
        return fifaLeagueRepositoryPort.listActiveLeagues()
    }

    override fun getLastResultTime(): Long {
    val latestMatch = fifaMatchRepositoryPort.findLatestMatch()
    if (latestMatch != null) {
        return latestMatch.time.time / 1000
    } else {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -60)
        val aWeekAgo = calendar.time
        return aWeekAgo.time / 1000
    }
}

    override fun saveMatch(
        integrationId: Int,
        time: Int,
        status: Int,
        leagueId: Int,
        home: String,
        away: String,
        homeGoalsAtHalfTime: Int?,
        homeGoalsAtFullTime: Int?,
        awayGoalsAtHalfTime: Int?,
        awayGoalsAtFullTime: Int?,
        totalGoalsAtHalfTime: Int?,
        totalGoalsAtFullTime: Int?,
        winner: String?
    ) {
        val league = fifaLeagueRepositoryPort.findByIntegrationId(leagueId) ?: throw NotFoundException(
            "League",
            "id",
            leagueId.toString()
        )

        val matchDate = Date(time.toLong() * 1000)
        val matchStatus = FifaMatchStatusTypes.entries.find { it.ordinal == status }
            ?: throw IllegalArgumentException("Invalid match status")

        val existingMatch = fifaMatchRepositoryPort.findByIntegrationId(integrationId)

        val match = existingMatch?.copy(
            time = matchDate,
            status = matchStatus,
            league = league,
            homeGoalsAtHalfTime = homeGoalsAtHalfTime,
            homeGoalsAtFullTime = homeGoalsAtFullTime,
            awayGoalsAtHalfTime = awayGoalsAtHalfTime,
            awayGoalsAtFullTime = awayGoalsAtFullTime,
            totalGoalsAtHalfTime = totalGoalsAtHalfTime,
            totalGoalsAtFullTime = totalGoalsAtFullTime
        ) ?: FifaMatch(
            integrationId = integrationId,
            time = matchDate,
            status = matchStatus,
            league = league,
            home = findOrCreatePlayer(home, league),
            away = findOrCreatePlayer(away, league),
            homeGoalsAtHalfTime = homeGoalsAtHalfTime,
            homeGoalsAtFullTime = homeGoalsAtFullTime,
            awayGoalsAtHalfTime = awayGoalsAtHalfTime,
            awayGoalsAtFullTime = awayGoalsAtFullTime,
            totalGoalsAtHalfTime = totalGoalsAtHalfTime,
            totalGoalsAtFullTime = totalGoalsAtFullTime,
            winner = determineWinner(winner, home, away, league)
        )

        fifaMatchRepositoryPort.save(match)
    }

    private fun findOrCreatePlayer(name: String, league: FifaLeague): FifaPlayer {
        return fifaPlayerRepositoryPort.findByName(name) ?: FifaPlayer(name = name, league = league).also {
            fifaPlayerRepositoryPort.save(it)
        }
    }

    private fun determineWinner(winner: String?, home: String, away: String, league: FifaLeague): FifaPlayer? {
        return when (winner) {
            home -> findOrCreatePlayer(home, league)
            away -> findOrCreatePlayer(away, league)
            else -> null
        }
    }
}