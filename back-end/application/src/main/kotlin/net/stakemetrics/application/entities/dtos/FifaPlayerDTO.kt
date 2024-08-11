package net.stakemetrics.application.entities.dtos

import java.util.UUID
import net.stakemetrics.application.entities.FifaPlayer

class FifaPlayerDTO {

    data class FifaPlayerResponse(
        val id: UUID,
        val leagueId: UUID?,
        val name: String
    )

}

fun FifaPlayer.toResponse(): FifaPlayerDTO.FifaPlayerResponse {
    return FifaPlayerDTO.FifaPlayerResponse(
        this.id, this.league?.id, this.name
    )
}