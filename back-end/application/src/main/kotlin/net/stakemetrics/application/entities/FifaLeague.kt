package net.stakemetrics.application.entities

import net.stakemetrics.application.entities.enums.FifaLeagueStatusTypes
import java.util.UUID

data class FifaLeague(
    val id: UUID = UUID.randomUUID(),
    val integrationId: Long,
    val status: FifaLeagueStatusTypes = FifaLeagueStatusTypes.ACTIVE,
    val name: String,
    val link: String
)
