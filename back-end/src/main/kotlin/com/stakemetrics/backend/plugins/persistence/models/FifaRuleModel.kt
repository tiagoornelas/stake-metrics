package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.fifa.FifaRule
import com.stakemetrics.backend.domain.enums.fifa.FifaMatchupTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaRuleTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyScopeTypes
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "fifa_rules")
data class FifaRuleModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val type: FifaRuleTypes = FifaRuleTypes.MINIMUM_ODDS,
    val value: Double = 0.0,
    val matchup: FifaMatchupTypes? = null,
    val scope: FifaStrategyScopeTypes? = null,
    val scopeValue: Int = 0
) {
    fun toDomain(): FifaRule {
        return FifaRule(
            id,
            type,
            value,
            matchup,
            scope,
            scopeValue
        )
    }
}
