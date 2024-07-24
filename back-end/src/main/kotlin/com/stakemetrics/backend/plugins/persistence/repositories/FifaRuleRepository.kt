package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.fifa.FifaRule
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy
import com.stakemetrics.backend.domain.ports.fifa.FifaRuleRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaRuleModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaRuleJpaRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class FifaRuleRepository(private val fifaRuleJpaRepository: FifaRuleJpaRepository) : FifaRuleRepositoryPort {
    override fun save(rule: FifaRule, strategy: FifaStrategy) {
        fifaRuleJpaRepository.save(rule.toModel(strategy))
    }

    @Transactional
    override fun saveAll(rules: List<FifaRule>, strategy: FifaStrategy) {
        fifaRuleJpaRepository.saveAll(rules.map { it.toModel(strategy) })
    }
}

fun FifaRule.toModel(strategy: FifaStrategy): FifaRuleModel {
    return FifaRuleModel(
        id,
        type,
        value,
        matchup,
        scope,
        strategy.toModel()
    )
}