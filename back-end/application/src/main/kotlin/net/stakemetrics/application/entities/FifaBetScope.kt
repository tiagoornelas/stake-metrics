package net.stakemetrics.application.entities

import java.util.UUID
import net.stakemetrics.application.entities.enums.FifaMatchupTypes
import net.stakemetrics.application.entities.enums.FifaStrategyScopeTypes

data class FifaBetScope(
    val id: UUID = UUID.randomUUID(),
    val matchup: FifaMatchupTypes? = null,
    val type: FifaStrategyScopeTypes? = null,
    val value: Int? = null,
    val rules: MutableSet<FifaBetRule> = mutableSetOf()
)