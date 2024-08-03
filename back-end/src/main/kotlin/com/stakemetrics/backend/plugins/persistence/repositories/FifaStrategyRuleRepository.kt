package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyRule
import com.stakemetrics.backend.domain.ports.fifa.FifaStrategyRuleRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaStrategyRuleModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaStrategyRuleJpaRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class FifaStrategyRuleRepository(private val fifaStrategyRuleJpaRepository: FifaStrategyRuleJpaRepository) :
    FifaStrategyRuleRepositoryPort {
    @Transactional
    override fun saveAll(rules: List<FifaStrategyRule>) {
        fifaStrategyRuleJpaRepository.saveAll(rules.map { it.toModel() })
    }
}

fun FifaStrategyRule.toModel(): FifaStrategyRuleModel {
    return FifaStrategyRuleModel(
        id,
        type,
        value
    )
}