package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyStatus
import com.stakemetrics.backend.plugins.persistence.models.FifaStrategyModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface FifaStrategyJpaRepository : JpaRepository<FifaStrategyModel, UUID> {
    fun findAllByStatusIn(statuses: List<FifaStrategyStatus>): List<FifaStrategyModel>
    fun findAllByUserId(userId: UUID): List<FifaStrategyModel>
    fun countByUserId(userId: UUID): Int
    fun countByUserIdAndStatus(userId: UUID, status: FifaStrategyStatus): Int
}