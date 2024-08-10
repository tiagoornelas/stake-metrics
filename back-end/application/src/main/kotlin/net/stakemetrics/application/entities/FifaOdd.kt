package net.stakemetrics.application.entities

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.enums.FifaMarketTypes

data class FifaOdd(
    val id: UUID = UUID.randomUUID(),
    val match: FifaMatch,
    val marketType: FifaMarketTypes,
    val updateTime: Date,
    val handicap: Double? = null,
    val home: Double? = null,
    val draw: Double? = null,
    val away: Double? = null,
    val over: Double? = null,
    val under: Double? = null,
)
