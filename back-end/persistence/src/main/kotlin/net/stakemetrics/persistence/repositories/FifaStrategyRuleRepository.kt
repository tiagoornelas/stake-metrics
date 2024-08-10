package net.stakemetrics.persistence.repositories

import jakarta.transaction.Transactional
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.repositories.IFifaStrategyRuleRepository
import net.stakemetrics.persistence.jpa.FifaStrategyRuleJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class FifaStrategyRuleRepository(private var fifaStrategyRuleJpaRepository: FifaStrategyRuleJpaRepository) :
    IFifaStrategyRuleRepository {

    @Transactional
    override fun saveAll(rules: List<FifaStrategyRule>) {
        fifaStrategyRuleJpaRepository.saveAll(rules.map { it.toModel() })
    }
}
