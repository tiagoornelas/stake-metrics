package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.persistence.models.FifaBetModel
import net.stakemetrics.persistence.models.FifaStrategyModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FifaBetJpaRepository : JpaRepository<FifaBetModel, UUID> {
    fun existsByStrategyAndMatchIntegrationId(strategy: FifaStrategyModel, matchIntegrationId: Long): Boolean
    fun findAllByStrategyIdInOrderByMatchTimeDesc(strategyIds: Collection<UUID>, pageable: Pageable): Page<FifaBetModel>
    fun deleteAllByStrategyId(strategyId: UUID)

    @Query("SELECT f FROM FifaBetModel f WHERE f.strategy.id IN :strategyIds AND f.isPaperBet = false ORDER BY f.match.time DESC")
    fun findAllByStrategyIdInAndIsNotPaperBetOrderByMatchTimeDesc(
        strategyIds: Collection<UUID>,
        pageable: Pageable
    ): Page<FifaBetModel>

    fun findAllByStatusOrProfit(status: BetStatusTypes, profit: Double?): List<FifaBetModel>
}