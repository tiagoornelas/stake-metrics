package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyRule
import com.stakemetrics.backend.domain.enums.fifa.FifaRuleTypes
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "fifa_strategy_rules")
data class FifaStrategyRuleModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val type: FifaRuleTypes = FifaRuleTypes.MINIMUM_ODDS,
    val value: Double = 0.0
) {
    fun toDomain(): FifaStrategyRule {
        return FifaStrategyRule(
            id,
            type,
            value
        )
    }
}
