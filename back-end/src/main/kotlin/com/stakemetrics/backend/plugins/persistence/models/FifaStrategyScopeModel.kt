package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyScope
import com.stakemetrics.backend.domain.enums.fifa.FifaMatchupTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyScopeTypes
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "fifa_strategy_scopes")
data class FifaStrategyScopeModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val matchup: FifaMatchupTypes? = null,
    val type: FifaStrategyScopeTypes? = null,
    val value: Int = 0,
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "fifa_strategy_scope_id")
    var rules: MutableSet<FifaStrategyRuleModel> = mutableSetOf(),
) {
    fun toDomain(): FifaStrategyScope {
        return FifaStrategyScope(
            id,
            matchup,
            type,
            value,
            rules.map { it.toDomain() }.toMutableSet()
        )
    }
}
