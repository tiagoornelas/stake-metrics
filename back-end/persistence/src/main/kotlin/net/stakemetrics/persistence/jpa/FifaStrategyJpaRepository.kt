package net.stakemetrics.persistence.jpa

import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import net.stakemetrics.persistence.models.FifaStrategyModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface FifaStrategyJpaRepository : JpaRepository<FifaStrategyModel, UUID> {
    fun findAllByStatusIn(statuses: List<FifaStrategyStatus>): List<FifaStrategyModel>
    fun findAllByUserId(userId: UUID): List<FifaStrategyModel>
    fun findAllByUserIdAndStatusIn(userId: UUID, statuses: List<FifaStrategyStatus>): List<FifaStrategyModel>
    fun countByUserId(userId: UUID): Int
    fun countByUserIdAndStatus(userId: UUID, status: FifaStrategyStatus): Int
    @Query(name = "find_strategy_statistics_by_user_id", nativeQuery = true)
    fun findStrategyStatisticsByUserId(
        @Param("userId") userId: UUID,
        @Param("timezone") timezone: String
    ): List<FifaStrategyDTO.FifaStrategyStatisticSingleResponse>
}