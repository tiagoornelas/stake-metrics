package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyScope
import com.stakemetrics.backend.domain.ports.fifa.FifaStrategyScopeRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaStrategyScopeModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaStrategyScopeJpaRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class FifaStrategyScopeRepository(private val fifaStrategyScopeJpaRepository: FifaStrategyScopeJpaRepository) :
    FifaStrategyScopeRepositoryPort {
    override fun save(rule: FifaStrategyScope) {
        fifaStrategyScopeJpaRepository.save(rule.toModel())
    }

    @Transactional
    override fun saveAll(rules: List<FifaStrategyScope>) {
        fifaStrategyScopeJpaRepository.saveAll(rules.map { it.toModel() })
    }
}

fun FifaStrategyScope.toModel(): FifaStrategyScopeModel {
    return FifaStrategyScopeModel(
        id,
        matchup,
        type,
        value,
        rules.map { it.toModel() }.toMutableSet()
    )
}
