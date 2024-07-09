package com.stakemetrics.backend.plugins.http.dto

import com.stakemetrics.backend.domain.entities.FifaLeague
import com.stakemetrics.backend.domain.enums.FifaMatchStatusTypes
import java.util.UUID

class FifaDTO {
    data class LastResultTimeResponse(
        val lastResultTime: Long? = null,
        val success: Boolean = true,
    )

    data class FifaLeagueResponse(
        val id: UUID,
        val integrationId: Int,
        val name: String,
        val link: String
    )

    data class FifaLeaguesResponse(
        val leagues: List<FifaLeagueResponse> = emptyList(),
        val success: Boolean = true,
    )

    data class FifaMatchRequest(
        val integrationId: Int,
        val time: Int,
        val status: Int,
        val leagueId: Int,
        val home: String,
        val away: String,
        val homeGoalsAtHalfTime: Int? = null,
        val homeGoalsAtFullTime: Int? = null,
        val awayGoalsAtHalfTime: Int? = null,
        val awayGoalsAtFullTime: Int? = null,
        val totalGoalsAtHalfTime: Int? = null,
        val totalGoalsAtFullTime: Int? = null,
        val winner: String? = null
    )

    data class FifaMatchResponse(
        val id: UUID,
        val success: Boolean = true
    )
}

fun FifaLeague.toResponse(): FifaDTO.FifaLeagueResponse {
    return FifaDTO.FifaLeagueResponse(
        this.id, this.integrationId, this.name, this.link
    )
}