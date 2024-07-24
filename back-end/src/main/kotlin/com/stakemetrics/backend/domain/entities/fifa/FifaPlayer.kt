package com.stakemetrics.backend.domain.entities.fifa

import java.util.UUID

data class FifaPlayer(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val league: FifaLeague? = null
)
