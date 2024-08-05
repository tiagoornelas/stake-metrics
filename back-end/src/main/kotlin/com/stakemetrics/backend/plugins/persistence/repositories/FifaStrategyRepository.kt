package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyStatus
import com.stakemetrics.backend.domain.ports.fifa.FifaStrategyRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaStrategyModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaStrategyJpaRepository
import java.util.UUID
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@Repository
class FifaStrategyRepository(
    private val fifaStrategyJpaRepository: FifaStrategyJpaRepository
) : FifaStrategyRepositoryPort {
    @CacheEvict(value = ["strategies"], allEntries = true)
    override fun save(strategy: FifaStrategy) {
        fifaStrategyJpaRepository.save(strategy.toModel())
    }

    @Cacheable("strategies")
    override fun getAllProneToBetStrategies(): List<FifaStrategy> {
        val proneToBetStatuses = listOf(FifaStrategyStatus.ACTIVE, FifaStrategyStatus.PAPER_BET)
        return fifaStrategyJpaRepository.findAllByStatusIn(proneToBetStatuses).map { it.toDomain() }
    }

    override fun getStrategiesByUser(userId: UUID): List<FifaStrategy> {
        return fifaStrategyJpaRepository.findAllByUserId(userId).map { it.toDomain() }
    }

    override fun findById(id: UUID): FifaStrategy? {
        return fifaStrategyJpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    @CacheEvict(value = ["strategies"], allEntries = true)
    override fun delete(strategy: FifaStrategy) {
        return fifaStrategyJpaRepository.delete(strategy.toModel())
    }

    override fun countByUser(userId: UUID): Int {
        return fifaStrategyJpaRepository.countByUserId(userId)
    }

    override fun countByUserAndStatus(userId: UUID, status: FifaStrategyStatus): Int {
        return fifaStrategyJpaRepository.countByUserIdAndStatus(userId, status)
    }

}

fun FifaStrategy.toModel(): FifaStrategyModel {
    return FifaStrategyModel(
        id = this.id,
        status = this.status,
        name = this.name,
        marketType = this.marketType,
        marketSubTypes = this.marketSubTypes,
        leagues = this.leagues.map { it.toModel() }.toMutableSet(),
        excludedPlayers = this.excludedPlayers.map { it.toModel() }.toMutableSet(),
        scopes = this.scopes.map { it.toModel() }.toMutableSet(),
        user = this.user?.toModel()
    )
}