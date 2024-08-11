package net.stakemetrics.application.entities.dtos

import java.util.UUID
import net.stakemetrics.application.entities.FifaLeague

class FifaLeagueDTO {

    data class FifaLeagueResponse(
        val id: UUID,
        val integrationId: Long,
        val name: String,
        val link: String
    )

}

fun FifaLeague.toResponse(): FifaLeagueDTO.FifaLeagueResponse {
    return FifaLeagueDTO.FifaLeagueResponse(
        this.id, this.integrationId, this.name, this.link
    )
}
