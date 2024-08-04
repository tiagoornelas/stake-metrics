package com.stakemetrics.backend.domain.services.fifa

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.entities.fifa.*
import com.stakemetrics.backend.domain.enums.fifa.*
import com.stakemetrics.backend.domain.exceptions.NotFoundException
import com.stakemetrics.backend.domain.ports.fifa.FifaLeagueRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaMatchRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaPlayerRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaStrategyRepositoryPort
import com.stakemetrics.backend.domain.services.UserService
import com.stakemetrics.backend.domain.services.fifa.workers.FifaStrategyEnqueuer
import com.stakemetrics.backend.domain.services.fifa.workers.FifaStrategyValidator
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import com.stakemetrics.backend.plugins.http.dto.toResponse
import com.stakemetrics.backend.plugins.http.ports.FifaServicePort
import jakarta.servlet.http.HttpServletRequest
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.random.Random

class FifaService(
    private val userService: UserService,
    private val fifaStrategyValidator: FifaStrategyValidator,
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
        fifaStrategyValidator.validate(dto)

        val user = getUser(userEmail)
        fifaStrategyValidator.checkIfUserCanCreate(user)

        val leagues = getLeagues(dto.leagues)
        val players = getPlayers(dto.excludedPlayers)

        val scopes = dto.scopes.map { scopeRequest ->
            val rules = scopeRequest.rules.map { ruleRequest ->
                FifaStrategyRule(
                    id = ruleRequest.id ?: UUID.randomUUID(),
                    type = ruleRequest.type,
                    value = ruleRequest.value
                )
            }.toMutableSet()

            FifaStrategyScope(
                id = scopeRequest.id ?: UUID.randomUUID(),
                matchup = scopeRequest.matchup,
                type = scopeRequest.type,
                value = scopeRequest.value,
                rules = rules
            )
        }.toMutableSet()

        val strategy = FifaStrategy(
            id = dto.id ?: UUID.randomUUID(),
            name = dto.name,
            marketType = dto.marketType,
            marketSubTypes = dto.marketSubTypes.toMutableSet(),
            leagues = leagues,
            excludedPlayers = players,
            scopes = scopes,
            user = user
        )

        fifaStrategyRepositoryPort.save(strategy)
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

    override fun enqueueStrategiesAgainstOdds(odds: FifaDTO.FifaOddRequest, request: HttpServletRequest) {
        fifaStrategyEnqueuer.enqueue(odds, request)
    }

    override fun updateStrategyStatus(userEmail: String, strategyId: UUID, status: FifaStrategyStatus) {
        val strategy = fifaStrategyRepositoryPort.findById(strategyId)
            ?: throw NotFoundException("Strategy", "id", strategyId.toString())

        val user = getUser(userEmail)
        fifaStrategyValidator.assureStrategyBelongsToUser(strategy, user)
        fifaStrategyValidator.canUserChangeStatus(user, status)

        strategy.status = status
        fifaStrategyRepositoryPort.save(strategy)
    }

    override fun deleteStrategy(userEmail: String, strategyId: UUID) {
        val strategy = fifaStrategyRepositoryPort.findById(strategyId)
            ?: throw NotFoundException("Strategy", "id", strategyId.toString())

        val user = getUser(userEmail)
        fifaStrategyValidator.assureStrategyBelongsToUser(strategy, user)
        fifaStrategyRepositoryPort.delete(strategy)
    }

    override fun getStrategyParams(): FifaDTO.FifaStrategyParamsResponse {
        val leagues = listActiveLeagues().map { it.toResponse() }
        val players = fifaPlayerRepositoryPort.findAll().map { it.toResponse() }

        val ruleTypes = FifaRuleTypes.entries.toList()
        val matchupTypes = FifaMatchupTypes.entries.toList()
        val scopeTypes = FifaStrategyScopeTypes.entries.toList()
        val marketTypes = FifaMarketTypes.entries.map { marketType ->
            val subTypes = FifaMarketSubTypes.entries.filter { it.parentType == marketType }
            FifaDTO.FifaMarketTypeResponse(marketType, subTypes.toList())
        }

        return FifaDTO.FifaStrategyParamsResponse(
            leagues = leagues,
            marketTypes = marketTypes,
            players = players,
            ruleTypes = ruleTypes.map { it.toResponse() },
            matchupTypes = matchupTypes,
            scopeTypes = scopeTypes
        )
    }

    override fun getStrategy(userEmail: String, strategyId: UUID): FifaDTO.FifaStrategyReadResponse {
        val strategy = fifaStrategyRepositoryPort.findById(strategyId)
            ?: throw NotFoundException("Strategy", "id", strategyId.toString())

        val user = getUser(userEmail)
        fifaStrategyValidator.assureStrategyBelongsToUser(strategy, user)

        return FifaDTO.FifaStrategyReadResponse(
            strategy.id,
            strategy.name,
            strategy.marketType,
            strategy.marketSubTypes.toList(),
            strategy.leagues.map { it.toResponse() },
            strategy.excludedPlayers.map { it.toResponse() },
            strategy.scopes.map { it.toResponse() })
    }

    override fun listAllStrategies(userEmail: String): List<FifaDTO.FifaStrategySingleResponse> {
        val user = getUser(userEmail)
        val strategies = fifaStrategyRepositoryPort.getStrategiesByUser(user.id)

        val sortedStrategies = strategies.sortedWith(
            compareBy({ it.status == FifaStrategyStatus.INACTIVE },
                { it.status == FifaStrategyStatus.PAPER_BET },
                { it.status == FifaStrategyStatus.ACTIVE })
        )

        return sortedStrategies.map { strategy ->
            FifaDTO.FifaStrategySingleResponse(
                strategy.id,
                strategy.name,
                strategy.status,
                Random.nextInt(1, 500),
                Random.nextDouble(-100.0, 100.0),
                Random.nextDouble(-100.0, 100.0),
                Random.nextDouble(-100.0, 100.0),
                Random.nextDouble(-100.0, 100.0)
            )
        }
    }
}