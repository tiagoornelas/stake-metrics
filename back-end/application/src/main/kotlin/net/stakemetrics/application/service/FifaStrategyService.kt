package net.stakemetrics.application.service

import java.util.UUID
import kotlin.random.Random
import net.stakemetrics.application.entities.*
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.dtos.toResponse
import net.stakemetrics.application.entities.enums.*
import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IFifaStrategyRepository
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.application.workers.FifaPastResultsSearcher
import net.stakemetrics.application.workers.FifaStrategyResourceValidator
import net.stakemetrics.application.workers.tipsters.factory.FifaTipster
import net.stakemetrics.application.workers.tipsters.factory.FifaTipsterFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class FifaStrategyService @Autowired constructor(
    private val userService: UserService,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaPlayerService: FifaPlayerService,
    private val fifaStrategyRepository: IFifaStrategyRepository,
    @Lazy private val fifaStrategyResourceValidator: FifaStrategyResourceValidator,
    private val fifaPastResultsSearcher: FifaPastResultsSearcher,
    private val fifaTipsterFactory: FifaTipsterFactory,
    private val logger: Logger
) {

    fun save(userEmail: String, dto: FifaStrategyDTO.FifaStrategyRequest) {
        fifaStrategyResourceValidator.validate(dto)

        val user = userService.findByEmail(userEmail)
        fifaStrategyResourceValidator.checkIfUserCanCreate(user)

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
            status = getStrategyStatusOrDefault(dto.id),
            name = dto.name,
            marketType = dto.marketType,
            marketSubTypes = dto.marketSubTypes.toMutableSet(),
            leagues = leagues,
            excludedPlayers = players,
            scopes = scopes,
            user = user
        )

        fifaStrategyRepository.save(strategy)
    }

    private fun getStrategyStatusOrDefault(strategyId: UUID?): FifaStrategyStatus {
        return strategyId?.let { findById(it).status } ?: FifaStrategyStatus.INACTIVE
    }

    private fun getLeagues(leagues: List<UUID>): MutableSet<FifaLeague> {
        return leagues.map { fifaLeagueService.findById(it) }.toMutableSet()
    }

    private fun getPlayers(players: List<UUID>): MutableSet<FifaPlayer> {
        return players.map { fifaPlayerService.findById(it) }.toMutableSet()
    }

    fun findById(id: UUID): FifaStrategy {
        return fifaStrategyRepository.findById(id) ?: throw NotFoundException("Strategy", "id", id.toString())
    }

    fun countByUser(user: User): Int {
        return fifaStrategyRepository.countByUser(user)
    }

    fun countByUserAndStatus(user: User, status: FifaStrategyStatus): Int {
        return fifaStrategyRepository.countByUserAndStatus(user, status)
    }

    fun updateStrategyStatus(userEmail: String, strategyId: UUID, status: FifaStrategyStatus) {
        val strategy = findById(strategyId)
        val user = userService.findByEmail(userEmail)
        fifaStrategyResourceValidator.assureStrategyBelongsToUser(strategy, user)
        fifaStrategyResourceValidator.canUserChangeStatus(user, status)

        strategy.status = status
        fifaStrategyRepository.save(strategy)
    }

    fun deleteStrategy(userEmail: String, strategyId: UUID) {
        val strategy = findById(strategyId)
        val user = userService.findByEmail(userEmail)
        fifaStrategyResourceValidator.assureStrategyBelongsToUser(strategy, user)
        fifaStrategyRepository.delete(strategy)
    }

    fun getStrategyParams(): FifaStrategyDTO.FifaStrategyParamsResponse {
        val leagues = fifaLeagueService.listActiveLeagues().map { it.toResponse() }
        val players = fifaPlayerService.findAll().map { it.toResponse() }

        val ruleTypes = FifaRuleTypes.entries.toList()
        val matchupTypes = FifaMatchupTypes.entries.toList()
        val scopeTypes = FifaStrategyScopeTypes.entries.toList()
        val marketTypes = FifaMarketTypes.entries.map { marketType ->
            val subTypes = FifaMarketSubTypes.entries.filter { it.parentType == marketType }
            FifaStrategyDTO.FifaMarketTypeResponse(marketType, subTypes.toList())
        }

        return FifaStrategyDTO.FifaStrategyParamsResponse(
            leagues = leagues,
            marketTypes = marketTypes,
            players = players,
            ruleTypes = ruleTypes.map { it.toResponse() },
            matchupTypes = matchupTypes,
            scopeTypes = scopeTypes
        )
    }

    fun getStrategy(userEmail: String, strategyId: UUID): FifaStrategyDTO.FifaStrategyReadResponse {
        val strategy = findById(strategyId)
        val user = userService.findByEmail(userEmail)
        fifaStrategyResourceValidator.assureStrategyBelongsToUser(strategy, user)

        return FifaStrategyDTO.FifaStrategyReadResponse(
            strategy.id,
            strategy.name,
            strategy.marketType,
            strategy.marketSubTypes.toList(),
            strategy.leagues.map { it.toResponse() },
            strategy.excludedPlayers.map { it.toResponse() },
            strategy.scopes.map { it.toResponse() })
    }

    fun listAllStrategies(userEmail: String): List<FifaStrategyDTO.FifaStrategySingleResponse> {
        val user = userService.findByEmail(userEmail)
        val strategies = fifaStrategyRepository.getStrategiesByUser(user.id)

        val sortedStrategies = strategies.sortedWith(
            compareBy({ it.status == FifaStrategyStatus.INACTIVE },
                { it.status == FifaStrategyStatus.PAPER_BET },
                { it.status == FifaStrategyStatus.ACTIVE })
        )

        return sortedStrategies.map { strategy ->
            FifaStrategyDTO.FifaStrategySingleResponse(
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

    fun getAllProneToBetStrategies(): List<FifaStrategy> {
        return fifaStrategyRepository.getAllProneToBetStrategies()
    }

    fun runStrategyAgainstOdds(request: FifaStrategyDTO.FifaStrategyAgainstOddRequest) {
        val resultsByScopes = fifaPastResultsSearcher.search(request)
        val tipster = fifaTipsterFactory.getTipster(request.strategy.marketType)
        iterateOverOpportunitiesToTipster(resultsByScopes, request, tipster)
    }

    private fun iterateOverOpportunitiesToTipster(
        resultsByScopes: MutableSet<FifaStrategyDTO.FifaStrategyScopePastResults>,
        request: FifaStrategyDTO.FifaStrategyAgainstOddRequest,
        tipster: FifaTipster
    ) {
        val matchupPlayerNames: Pair<String, String> = Pair(request.odds.homePlayerName, request.odds.awayPlayerName)

        resultsByScopes.forEach { (scope, results) ->
            scope?.let {
                request.strategy.marketSubTypes.forEach { marketSubType ->
                    try {
                        tipster.tip(matchupPlayerNames, marketSubType, scope.rules, request.odds.odds, results)
                    } catch (e: FifaStrategyRuleBreakException) {
                        logger.logFifaStrategyRuleBreak(e)
                    } catch (e: NotFoundException) {
                        logger.logError(e)
                    }
                }
            } ?: throw IllegalArgumentException("Scope is null when sending the results to the tipster")
        }
    }

}