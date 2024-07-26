package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy
import com.stakemetrics.backend.domain.ports.fifa.FifaStrategyRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaStrategyModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaStrategyJpaRepository
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
    override fun getAllStrategies(): List<FifaStrategy> {
        return fifaStrategyJpaRepository.findAll().map { it.toDomain() }
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
        rules = this.rules.map { it.toModel() }.toMutableSet(),
        user = this.user?.toModel()
    )
}