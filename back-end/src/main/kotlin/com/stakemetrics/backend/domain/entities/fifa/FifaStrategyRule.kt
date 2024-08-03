package com.stakemetrics.backend.domain.entities.fifa

import com.stakemetrics.backend.domain.enums.fifa.FifaRuleTypes
import java.util.UUID

data class FifaStrategyRule(
    val id: UUID = UUID.randomUUID(),
    val type: FifaRuleTypes,
    val value: Double = 0.0
)
