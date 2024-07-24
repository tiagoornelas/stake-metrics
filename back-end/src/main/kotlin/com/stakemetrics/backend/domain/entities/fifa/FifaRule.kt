package com.stakemetrics.backend.domain.entities.fifa

import com.stakemetrics.backend.domain.enums.fifa.FifaMatchupTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaRuleTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyScopeTypes
import java.util.UUID

data class FifaRule(
    val id: UUID = UUID.randomUUID(),
    val type: FifaRuleTypes,
    val value: Double = 0.0,
    val matchup: FifaMatchupTypes? = null,
    val scope: FifaStrategyScopeTypes? = null,
)
