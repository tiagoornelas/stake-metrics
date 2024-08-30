package net.stakemetrics.application.entities

import net.stakemetrics.application.entities.enums.FifaMatchupTypes
import net.stakemetrics.application.entities.enums.FifaStrategyScopeTypes
import java.util.UUID

data class FifaStrategyScope(
    val id: UUID = UUID.randomUUID(),
    val matchup: FifaMatchupTypes? = null,
    val type: FifaStrategyScopeTypes? = null,
    val rules: MutableSet<FifaStrategyRule> = mutableSetOf()
)
