package net.stakemetrics.persistence.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import net.stakemetrics.application.entities.FifaBetRule
import net.stakemetrics.application.entities.enums.FifaRuleTypes

@Entity
@Table(name = "fifa_bet_rules")
data class FifaBetRuleModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val type: FifaRuleTypes = FifaRuleTypes.MINIMUM_ODDS,
    val value: Double = 0.0,
    val betValue: Double = 0.0
) {
    fun toDomain(): FifaBetRule {
        return FifaBetRule(
            id,
            type,
            value,
            betValue
        )
    }
}

fun FifaBetRule.toModel(): FifaBetRuleModel {
    return FifaBetRuleModel(
        id,
        type,
        value,
        betValue
    )
}