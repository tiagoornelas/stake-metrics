package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.persistence.models.FifaBetModel
import net.stakemetrics.persistence.models.FifaStrategyModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface FifaBetJpaRepository : JpaRepository<FifaBetModel, UUID> {
    fun existsByStrategyAndMatchIntegrationId(strategy: FifaStrategyModel, matchIntegrationId: Long): Boolean
    fun findAllByStrategyIdInOrderByMatchTimeDesc(strategyIds: Collection<UUID>, pageable: Pageable): Page<FifaBetModel>
    fun findAllByStatusOrProfit(status: BetStatusTypes, profit: Double?): List<FifaBetModel>
}