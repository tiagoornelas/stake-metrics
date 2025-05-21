package net.stakemetrics.application.repositories

import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import java.util.UUID

interface IFifaStrategyRepository {
    fun save(strategy: FifaStrategy)
    fun saveAll(strategies: List<FifaStrategy>)
    fun getAllProneToBetStrategies(): List<FifaStrategy>
    fun getStrategiesByUser(userId: UUID): List<FifaStrategy>
    fun getStrategiesStatisticsByUser(userId: UUID, timezone: String): List<FifaStrategyDTO.FifaStrategyStatisticSingleResponse>
    fun findById(id: UUID): FifaStrategy?
    fun findActiveByUser(user: User): List<FifaStrategy>
    fun findAllByUserId(userId: UUID): List<FifaStrategy>
    fun delete(strategy: FifaStrategy)
    fun countByUser(user: User): Int
    fun countByUserAndStatus(user: User, status: FifaStrategyStatus): Int
}