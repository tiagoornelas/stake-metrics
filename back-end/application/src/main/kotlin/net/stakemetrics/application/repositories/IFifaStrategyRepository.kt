package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.DataDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.enums.FifaStrategyStatus

interface IFifaStrategyRepository {
    fun save(strategy: FifaStrategy)
    fun getAllProneToBetStrategies(): List<FifaStrategy>
    fun getStrategiesByUser(userId: UUID): List<FifaStrategy>
    fun getStrategiesStatisticsByUser(userId: UUID, timezone: String): List<FifaStrategyDTO.FifaStrategyStatisticSingleResponse>
    fun getAllStrategiesByLeaguePerformance(): List<DataDTO.FifaStrategiesByLeaguePerformanceSingleResponse>
    fun findById(id: UUID): FifaStrategy?
    fun findActiveByUser(user: User): List<FifaStrategy>
    fun delete(strategy: FifaStrategy)
    fun countByUser(user: User): Int
    fun countByUserAndStatus(user: User, status: FifaStrategyStatus): Int
}