package net.stakemetrics.application.entities

import java.util.UUID
import net.stakemetrics.application.entities.enums.FifaRuleTypes

class FifaBetRule(
    val id: UUID = UUID.randomUUID(),
    val type: FifaRuleTypes,
    val value: Double = 0.0,
    val betValue: Double = 0.0
)