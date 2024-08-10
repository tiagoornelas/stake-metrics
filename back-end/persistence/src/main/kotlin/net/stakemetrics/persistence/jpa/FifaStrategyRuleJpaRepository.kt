package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.persistence.models.FifaStrategyRuleModel
import org.springframework.data.jpa.repository.JpaRepository

interface FifaStrategyRuleJpaRepository : JpaRepository<FifaStrategyRuleModel, UUID> {
}