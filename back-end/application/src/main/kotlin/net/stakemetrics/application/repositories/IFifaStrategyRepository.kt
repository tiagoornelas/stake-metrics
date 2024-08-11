package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.enums.FifaStrategyStatus

interface IFifaStrategyRepository {
    fun save(strategy: FifaStrategy)
    fun getAllProneToBetStrategies(): List<FifaStrategy>
    fun getStrategiesByUser(userId: UUID): List<FifaStrategy>
    fun findById(id: UUID): FifaStrategy?
    fun delete(strategy: FifaStrategy)
    fun countByUser(user: User): Int
    fun countByUserAndStatus(user: User, status: FifaStrategyStatus): Int
}