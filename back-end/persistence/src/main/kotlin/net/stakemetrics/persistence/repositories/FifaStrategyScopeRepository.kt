package net.stakemetrics.persistence.repositories

import jakarta.transaction.Transactional
import net.stakemetrics.application.entities.FifaStrategyScope
import net.stakemetrics.application.repositories.IFifaStrategyScopeRepository
import net.stakemetrics.persistence.jpa.FifaStrategyScopeJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class FifaStrategyScopeRepository(private val fifaStrategyScopeJpaRepository: FifaStrategyScopeJpaRepository) :
    IFifaStrategyScopeRepository {

    override fun save(rule: FifaStrategyScope) {
        fifaStrategyScopeJpaRepository.save(rule.toModel())
    }

    @Transactional
    override fun saveAll(rules: List<FifaStrategyScope>) {
        fifaStrategyScopeJpaRepository.saveAll(rules.map { it.toModel() })
    }
}

