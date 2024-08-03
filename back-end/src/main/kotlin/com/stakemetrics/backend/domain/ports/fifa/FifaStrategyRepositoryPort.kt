package com.stakemetrics.backend.domain.ports.fifa

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy
import java.util.UUID

interface FifaStrategyRepositoryPort {
    fun save(strategy: FifaStrategy)
    fun getAllStrategies(): List<FifaStrategy>
    fun getStrategiesByUser(userId: UUID): List<FifaStrategy>
    fun findById(id: UUID): FifaStrategy?
    fun delete(strategy: FifaStrategy)
}