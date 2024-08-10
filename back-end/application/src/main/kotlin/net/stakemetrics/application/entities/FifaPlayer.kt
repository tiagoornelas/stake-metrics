package net.stakemetrics.application.entities

import java.util.UUID

data class FifaPlayer(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val league: FifaLeague? = null
)
