package net.stakemetrics.application.entities

import net.stakemetrics.application.entities.enums.MatchupTypes
import net.stakemetrics.application.entities.enums.StrategyScopeTypes

interface StrategyScope {
    val matchup: MatchupTypes?
    val type: StrategyScopeTypes?
}
