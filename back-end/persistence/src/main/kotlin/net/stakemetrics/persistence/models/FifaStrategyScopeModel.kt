package net.stakemetrics.persistence.models

import net.stakemetrics.application.entities.enums.FifaMatchupTypes
import net.stakemetrics.application.entities.enums.FifaStrategyScopeTypes
import jakarta.persistence.*
import java.util.UUID
import net.stakemetrics.application.entities.FifaStrategyScope

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

fun FifaStrategyScope.toModel(): FifaStrategyScopeModel {
    return FifaStrategyScopeModel(
        id,
        matchup,
        type,
        value,
        rules.map { it.toModel() }.toMutableSet()
    )
}
