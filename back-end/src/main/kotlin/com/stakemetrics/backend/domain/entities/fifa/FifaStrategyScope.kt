package com.stakemetrics.backend.domain.entities.fifa

import com.stakemetrics.backend.domain.enums.fifa.FifaMatchupTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyScopeTypes
import java.util.UUID

data class FifaStrategyScope(
    val id: UUID = UUID.randomUUID(),
    val matchup: FifaMatchupTypes? = null,
    val type: FifaStrategyScopeTypes? = null,
    val value: Int = 0,
    val rules: MutableSet<FifaStrategyRule> = mutableSetOf()
)
