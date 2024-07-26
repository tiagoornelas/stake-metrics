package com.stakemetrics.backend.domain.services.fifa

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.entities.fifa.FifaLeague
import com.stakemetrics.backend.domain.entities.fifa.FifaMatch
import com.stakemetrics.backend.domain.entities.fifa.FifaPlayer
import com.stakemetrics.backend.domain.entities.fifa.FifaRule
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy
import com.stakemetrics.backend.domain.enums.fifa.FifaMatchStatusTypes
import com.stakemetrics.backend.domain.exceptions.NotFoundException
import com.stakemetrics.backend.domain.ports.fifa.FifaLeagueRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaMatchRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaPlayerRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaStrategyRepositoryPort
import com.stakemetrics.backend.domain.services.UserService
import com.stakemetrics.backend.domain.services.fifa.workers.FifaStrategyEnqueuer
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import com.stakemetrics.backend.plugins.http.ports.FifaServicePort
import jakarta.servlet.http.HttpServletRequest
import java.util.Calendar
import java.util.Date
import java.util.UUID

class FifaService(
    private val userService: UserService,
    private val fifaStrategyEnqueuer: FifaStrategyEnqueuer,
    private val fifaLeagueRepositoryPort: FifaLeagueRepositoryPort,
    private val fifaMatchRepositoryPort: FifaMatchRepositoryPort,
    private val fifaPlayerRepositoryPort: FifaPlayerRepositoryPort,
    private val fifaStrategyRepositoryPort: FifaStrategyRepositoryPort
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
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val aWeekAgo = calendar.time
            return aWeekAgo.time / 1000
        }
    }

    override fun saveMatch(dto: FifaDTO.FifaMatchRequest) {
        val league = fifaLeagueRepositoryPort.findByIntegrationId(dto.leagueId) ?: throw NotFoundException(
            "League",
            "id",
            dto.leagueId.toString()
        )

        val matchDate = Date(dto.time.toLong() * 1000)
        val matchStatus = FifaMatchStatusTypes.entries.find { it.ordinal == dto.status }
            ?: throw IllegalArgumentException("Invalid match status")

        val existingMatch = fifaMatchRepositoryPort.findByIntegrationId(dto.integrationId)

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
            winner = determineWinner(dto.winner, dto.home, dto.away, league)
        )

        fifaMatchRepositoryPort.save(match)
    }

    override fun saveStrategy(userEmail: String, dto: FifaDTO.FifaStrategyRequest) {
        val user = getUser(userEmail)
        val leagues = getLeagues(dto.leagues)
        val players = getPlayers(dto.excludedPlayers)

        val ruleEntities = dto.rules.map { ruleRequest ->
            FifaRule(
                type = ruleRequest.type,
                value = ruleRequest.value,
                matchup = ruleRequest.matchup,
                scope = ruleRequest.scope
            )
        }.toMutableSet()

        val strategy = FifaStrategy(
            name = dto.name,
            marketType = dto.marketType,
            marketSubTypes = dto.marketSubTypes.toMutableSet(),
            leagues = leagues,
            excludedPlayers = players,
            rules = ruleEntities,
            user = user
        )

        fifaStrategyRepositoryPort.save(strategy)
    }

    override fun enqueueStrategiesAgainstOdds(odds: FifaDTO.FifaOddRequest, request: HttpServletRequest) {
        fifaStrategyEnqueuer.enqueue(odds, request)
    }

    private fun getUser(email: String): User {
        return userService.findByEmail(email) ?: throw NotFoundException("User", "email", email)
    }

    private fun getLeagues(leagues: List<UUID>): MutableSet<FifaLeague> {
        return leagues.map {
            fifaLeagueRepositoryPort.findById(it) ?: throw NotFoundException(
                "League",
                "id",
                it.toString()
            )
        }
            .toMutableSet()
    }

    private fun getPlayers(players: List<UUID>): MutableSet<FifaPlayer> {
        return players.map {
            fifaPlayerRepositoryPort.findById(it) ?: throw NotFoundException(
                "Player",
                "id",
                it.toString()
            )
        }
            .toMutableSet()
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