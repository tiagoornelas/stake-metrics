package net.stakemetrics.application.entities

import java.util.UUID
import net.stakemetrics.application.entities.enums.MatchupTypes
import net.stakemetrics.application.entities.enums.StrategyScopeTypes

data class FifaStrategyScope(
    val id: UUID = UUID.randomUUID(),
    val rules: MutableSet<FifaStrategyRule> = mutableSetOf(),
    override val matchup: MatchupTypes,
    override val type: StrategyScopeTypes
) : StrategyScope