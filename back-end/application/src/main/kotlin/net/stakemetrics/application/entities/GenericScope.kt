package net.stakemetrics.application.entities

import net.stakemetrics.application.entities.enums.MatchupTypes
import net.stakemetrics.application.entities.enums.StrategyScopeTypes

data class GenericScope(
    override val matchup: MatchupTypes,
    override val type: StrategyScopeTypes
) : StrategyScope
