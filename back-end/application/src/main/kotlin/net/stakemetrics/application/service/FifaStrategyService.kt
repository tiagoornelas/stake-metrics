package net.stakemetrics.application.service

import jakarta.transaction.Transactional
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.FifaStrategyScope
import net.stakemetrics.application.entities.FifaTrendScopeAnalysis
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.dtos.toResponse
import net.stakemetrics.application.entities.enums.FeatureTypes
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.enums.FifaRuleTypes
import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import net.stakemetrics.application.entities.enums.MatchupTypes
import net.stakemetrics.application.entities.enums.StrategyScopeTypes
import net.stakemetrics.application.entities.enums.toResponse
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
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID

@Service
class FifaStrategyService @Autowired constructor(
    private val userService: UserService,
    private val queueService: IQueueService,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaPlayerService: FifaPlayerService,
    private val fifaBetRepository: IFifaBetRepository,
    private val fifaTipsterFactory: FifaTipsterFactory,
    private val subscriptionService: ISubscriptionService,
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
        val strategies = fifaStrategyRepository.getStrategiesStatisticsByUser(user.id, user.timezoneOffset.id)

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
                strategy.averageDailyBets
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
        val allScopesAnalysis = request.oddSnapshot.trendScopeAnalysis
        val strategyScopesWithAnalysis = pickStrategyScopesOnly(request.strategy, allScopesAnalysis)
        val tipster = fifaTipsterFactory.getTipster(request.strategy.marketType)
        val suitableCandidate = fifaStrategyOpportunityIterator.iterate(
            tipster,
            request.oddSnapshot.fifaMatch,
            request.strategy,
            request.oddSnapshot,
            strategyScopesWithAnalysis,
        )

        if (suitableCandidate != null) {
            val betRequest = FifaBetDTO.BetRequest(
                strategy = request.strategy,
                fifaMatchId = request.oddSnapshot.fifaMatch.id,
                candidate = suitableCandidate,
                oddSnapshot = request.oddSnapshot
            )
            queueService.enqueueBetTask(betRequest)
        }
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

    @Transactional
    fun getSimpleReport(userEmail: String, strategyId: UUID): FifaStrategyDTO.SimpleReportResponse {
        val user = userService.findByEmail(userEmail)
        val strategy = fifaStrategyRepository.findById(strategyId)

        fifaStrategyResourceValidator.assureStrategyBelongsToUser(strategy!!, user)
        subscriptionService.hasFeature(user, FeatureTypes.SIMPLE_REPORT)

        val thirtyDaysAgo = Date.from(Instant.now().minus(30, ChronoUnit.DAYS))

        val bets: List<FifaStrategyDTO.SimpleReportBet> =
            fifaBetRepository.findByStrategyIdAndBetTimeAfter(strategyId, thirtyDaysAgo)

        return FifaStrategyDTO.SimpleReportResponse(bets)
    }

    @Transactional
    fun getDetailedReport(userEmail: String, strategyId: UUID): FifaStrategyDTO.DetailedReportResponse {
        val user = userService.findByEmail(userEmail)
        val strategy = fifaStrategyRepository.findById(strategyId)

        fifaStrategyResourceValidator.assureStrategyBelongsToUser(strategy!!, user)
        subscriptionService.hasFeature(user, FeatureTypes.DETAILED_REPORT)

        val thirtyDaysAgo = Date.from(Instant.now().minus(30, ChronoUnit.DAYS))

        val bets: List<FifaStrategyDTO.DetailedReportBet> =
            fifaBetRepository.findDetailedBetsByStrategyIdAndBetTimeAfter(strategyId, thirtyDaysAgo)

        return FifaStrategyDTO.DetailedReportResponse(bets)
    }

}