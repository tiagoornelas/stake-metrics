package net.stakemetrics.persistence.models

import net.stakemetrics.application.entities.enums.MatchupTypes
import net.stakemetrics.application.entities.enums.StrategyScopeTypes
import jakarta.persistence.*
import java.util.UUID
import net.stakemetrics.application.entities.FifaStrategyScope

@Entity
@Table(name = "fifa_strategy_scopes")
data class FifaStrategyScopeModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val matchup: MatchupTypes? = null,
    val type: StrategyScopeTypes? = null,
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "fifa_strategy_scope_id")
    var rules: MutableSet<FifaStrategyRuleModel> = mutableSetOf(),
) {
    fun toDomain(): FifaStrategyScope {
        return FifaStrategyScope(
            id,
            rules.map { it.toDomain() }.toMutableSet(),
            matchup,
            type
        )
    }
}

fun FifaStrategyScope.toModel(): FifaStrategyScopeModel {
    return FifaStrategyScopeModel(
        id,
        matchup,
        type,
        rules.map { it.toModel() }.toMutableSet()
    )
}
