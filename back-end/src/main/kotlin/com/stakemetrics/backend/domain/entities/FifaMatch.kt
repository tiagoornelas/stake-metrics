package com.stakemetrics.backend.domain.entities

import com.stakemetrics.backend.domain.enums.FifaMatchStatusTypes
import java.util.Date
import java.util.UUID

data class FifaMatch(
    val id: UUID = UUID.randomUUID(),
    val integrationId: Int,
    val time: Date,
    val status: FifaMatchStatusTypes = FifaMatchStatusTypes.PENDING,
    val league: FifaLeague,
    val home: FifaPlayer,
    val away: FifaPlayer,
    val homeGoalsAtHalfTime: Int? = null,
    val homeGoalsAtFullTime: Int? = null,
    val awayGoalsAtHalfTime: Int? = null,
    val awayGoalsAtFullTime: Int? = null,
    val totalGoalsAtHalfTime: Int? = null,
    val totalGoalsAtFullTime: Int? = null,
    val winner: FifaPlayer? = null,
)