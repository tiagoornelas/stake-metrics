package com.stakemetrics.backend.plugins.http.dto

import com.stakemetrics.backend.domain.entities.fifa.FifaLeague
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy
import com.stakemetrics.backend.domain.enums.fifa.FifaMarketSubTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaMarketTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaMatchupTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaRuleTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyScopeTypes
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
        val success: Boolean = true
    )

    data class FifaSingleOddRequest(
        val marketType: FifaMarketTypes,
        val updateTime: Int,
        val handicap: Double?,
        val home: Double?,
        val draw: Double?,
        val away: Double?,
        val over: Double?,
        val under: Double?,
    )

    data class FifaOddRequest(
        val integrationId: Int,
        val odds: List<FifaSingleOddRequest>
    )

    data class FifaOddResponse(
        val success: Boolean = true
    )

    data class FifaRuleRequest(
        val type: FifaRuleTypes,
        val matchup: FifaMatchupTypes? = null,
        val scope: FifaStrategyScopeTypes? = null,
        val value: Double,
        val scopeValue: Int,
    )

    data class FifaStrategyRequest(
        val name: String,
        val marketType: FifaMarketTypes,
        val marketSubTypes: List<FifaMarketSubTypes>,
        val leagues: List<UUID>,
        val excludedPlayers: List<UUID>,
        val rules: List<FifaRuleRequest>
    )

    data class FifaStrategyResponse(
        val success: Boolean = true
    )

    data class FifaStrategyAgainstOddRequest(
        val strategy: FifaStrategy,
        val odds: FifaOddRequest
    )

    data class FifaStrategyAgainstOddResponse(
        val success: Boolean = true
    )
}

fun FifaLeague.toResponse(): FifaDTO.FifaLeagueResponse {
    return FifaDTO.FifaLeagueResponse(
        this.id, this.integrationId, this.name, this.link
    )
}