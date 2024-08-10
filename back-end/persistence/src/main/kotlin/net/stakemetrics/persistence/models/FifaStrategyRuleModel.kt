package net.stakemetrics.persistence.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.enums.FifaRuleTypes

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

fun FifaStrategyRule.toModel(): FifaStrategyRuleModel {
    return FifaStrategyRuleModel(
        id,
        type,
        value
    )
}
