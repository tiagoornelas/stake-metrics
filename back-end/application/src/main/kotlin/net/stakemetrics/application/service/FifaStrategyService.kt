package net.stakemetrics.application.service

import jakarta.transaction.Transactional
import java.util.UUID
import net.stakemetrics.application.entities.*
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.dtos.toResponse
import net.stakemetrics.application.entities.enums.*
import net.stakemetrics.application.entities.exceptions.EntityDoesntBelongToUserException
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.repositories.IFifaStrategyRepository
import net.stakemetrics.application.workers.FifaStrategyOpportunityIterator
import net.stakemetrics.application.workers.FifaStrategyResourceValidator
import net.stakemetrics.application.workers.tipsters.factory.FifaTipsterFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class FifaStrategyService @Autowired constructor(
    private val userService: UserService,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaPlayerService: FifaPlayerService,
    private val fifaBetRepository: IFifaBetRepository,
    private val fifaTipsterFactory: FifaTipsterFactory,
    private val fifaOddSnapshotService: FifaOddSnapshotService,
    private val fifaStrategyRepository: IFifaStrategyRepository,
    private val fifaStrategyOpportunityIterator: FifaStrategyOpportunityIterator,
    @Lazy private val fifaStrategyResourceValidator: FifaStrategyResourceValidator,
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
                    id = ruleRequest.id ?: UUID.randomUUID(), type = ruleRequest.type, value = ruleRequest.value
                )
            }.toMutableSet()

            FifaStrategyScope(
                id = scopeRequest.id ?: UUID.randomUUID(),
                matchup = scopeRequest.matchup!!,
                type = scopeRequest.type!!,
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

    @Transactional
    fun restartStrategy(userEmail: String, strategyId: UUID) {
        val strategy = findById(strategyId)
        val user = userService.findByEmail(userEmail)
        fifaStrategyResourceValidator.assureStrategyBelongsToUser(strategy, user)
        fifaBetRepository.deleteAllByStrategyId(strategy.id)
    }

    @Transactional
    fun deleteStrategy(userEmail: String, strategyId: UUID) {
        val strategy = findById(strategyId)
        val user = userService.findByEmail(userEmail)
        fifaStrategyResourceValidator.assureStrategyBelongsToUser(strategy, user)
        fifaBetRepository.deleteAllByStrategyId(strategy.id)
        fifaStrategyRepository.delete(strategy)
    }

    fun getStrategyParams(): FifaStrategyDTO.FifaStrategyParamsResponse {
        val leagues = fifaLeagueService.listActiveLeagues().map { it.toResponse() }
        val players = fifaPlayerService.findAll().map { it.toResponse() }

        val ruleTypes = FifaRuleTypes.entries.toList()
        val matchupTypes = MatchupTypes.entries.toList()
        val scopeTypes = StrategyScopeTypes.entries.toList()
        val marketTypes = FifaMarketTypes.entries.map { marketType ->
            val subTypes = marketType.subTypes
            FifaStrategyDTO.FifaMarketTypeResponse(marketType, subTypes)
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

        return FifaStrategyDTO.FifaStrategyReadResponse(strategy.id,
            strategy.name,
            strategy.marketType,
            strategy.marketSubTypes.toList(),
            strategy.leagues.map { it.toResponse() },
            strategy.excludedPlayers.map { it.toResponse() },
            strategy.scopes.map { it.toResponse() })
    }

    fun listAllStrategiesStatistics(userEmail: String): List<FifaStrategyDTO.FifaStrategyStatisticSingleResponse> {
        val user = userService.findByEmail(userEmail)
        val strategies = fifaStrategyRepository.getStrategiesStatisticsByUser(user.id)

        val sortedStrategies = strategies.sortedWith(
            compareBy({ it.status == FifaStrategyStatus.INACTIVE },
                { it.status == FifaStrategyStatus.PAPER_BET },
                { it.status == FifaStrategyStatus.ACTIVE })
        )

        return sortedStrategies.map { strategy ->
            FifaStrategyDTO.FifaStrategyStatisticSingleResponse(
                strategy.id,
                strategy.name,
                strategy.status,
                strategy.openBets,
                strategy.bets,
                strategy.result,
                strategy.roi,
                strategy.activeResult,
                strategy.activeRoi,
                strategy.todaysResult,
                strategy.todaysRoi
            )
        }
    }

    fun listCumulativeProfits(userEmail: String, strategyId: UUID): List<Double> {
        val strategy = fifaStrategyRepository.findById(strategyId)
        val user = userService.findByEmail(userEmail)
        assetStrategyBelongsToUser(strategy!!, user)
        return fifaBetRepository.listCumulativeProfits(strategyId)
    }

    private fun assetStrategyBelongsToUser(strategy: FifaStrategy, user: User) {
        if (strategy.user?.id != user.id) throw EntityDoesntBelongToUserException()
    }

    fun findAllByUserId(userId: UUID): List<FifaStrategy> {
        return fifaStrategyRepository.getStrategiesByUser(userId)
    }

    fun runStrategyAgainstOdds(request: FifaStrategyDTO.FifaStrategyAgainstOddRequest) {
        val oddSnapshot = fifaOddSnapshotService.getById(request.oddSnapshotId)
        val allScopesAnalysis = oddSnapshot.trendScopeAnalysis
        val strategyScopesWithAnalysis = pickStrategyScopesOnly(request.strategy, allScopesAnalysis)
        val tipster = fifaTipsterFactory.getTipster(request.strategy.marketType)
        fifaStrategyOpportunityIterator.iterate(
            tipster, oddSnapshot.fifaMatch, request.strategy, oddSnapshot, strategyScopesWithAnalysis
        )
    }

    private fun pickStrategyScopesOnly(
        strategy: FifaStrategy,
        allScopeResults: MutableSet<FifaTrendScopeAnalysis>
    ): MutableSet<FifaTrendScopeAnalysis> {
        return allScopeResults.filter { scopeResult ->
            strategy.scopes.any { strategyScope ->
                scopeResult.matchup == strategyScope.matchup && scopeResult.type == strategyScope.type
            }
        }.toMutableSet()
    }

}
