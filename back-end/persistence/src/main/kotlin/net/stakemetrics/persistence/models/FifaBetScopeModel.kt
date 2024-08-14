package net.stakemetrics.persistence.models

import jakarta.persistence.*
import java.util.UUID
import net.stakemetrics.application.entities.FifaBetScope
import net.stakemetrics.application.entities.enums.FifaMatchupTypes
import net.stakemetrics.application.entities.enums.FifaStrategyScopeTypes

@Entity
@Table(name = "fifa_bet_scopes")
data class FifaBetScopeModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val matchup: FifaMatchupTypes? = null,
    val type: FifaStrategyScopeTypes? = null,
    val value: Int? = null,
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "fifa_bet_scope_id")
    var rules: MutableSet<FifaBetRuleModel> = mutableSetOf(),
) {
    fun toDomain(): FifaBetScope {
        return FifaBetScope(
            id,
            matchup,
            type,
            value,
            rules.map { it.toDomain() }.toMutableSet()
        )
    }
}

fun FifaBetScope.toModel(): FifaBetScopeModel {
    return FifaBetScopeModel(
        id,
        matchup,
        type,
        value,
        rules.map { it.toModel() }.toMutableSet()
    )
}
