package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import net.stakemetrics.persistence.models.FifaStrategyModel
import org.springframework.data.jpa.repository.JpaRepository

interface FifaStrategyJpaRepository : JpaRepository<FifaStrategyModel, UUID> {
    fun findAllByStatusIn(statuses: List<FifaStrategyStatus>): List<FifaStrategyModel>
    fun findAllByUserId(userId: UUID): List<FifaStrategyModel>
    fun countByUserId(userId: UUID): Int
    fun countByUserIdAndStatus(userId: UUID, status: FifaStrategyStatus): Int
}