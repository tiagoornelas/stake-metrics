package com.stakemetrics.backend.domain.entities

import com.stakemetrics.backend.domain.enums.FifaLeagueStatusTypes
import java.util.UUID

data class FifaLeague(
    val id: UUID = UUID.randomUUID(),
    val integrationId: Int,
    val status: FifaLeagueStatusTypes = FifaLeagueStatusTypes.ACTIVE,
    val name: String,
    val link: String,
)
