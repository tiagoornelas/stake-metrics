package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.fifa.FifaRule
import com.stakemetrics.backend.domain.enums.fifa.FifaMatchupTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaRuleTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyScopeTypes
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
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
    @ManyToOne @JoinColumn(name = "strategy_id")
    val strategy: FifaStrategyModel? = null
) {
    fun toDomain(): FifaRule {
        return FifaRule(
            id,
            type,
            value,
            matchup,
            scope
        )
    }
}
