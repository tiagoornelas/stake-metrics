package com.stakemetrics.backend.plugins.http.dto

import com.stakemetrics.backend.domain.entities.fifa.*
import com.stakemetrics.backend.domain.enums.fifa.*
import java.util.UUID

class FifaDTO {

    data class FifaLeagueResponse(
        val id: UUID,
        val integrationId: Int,
        val name: String,
        val link: String
    )

    data class FifaLeaguesResponse(
        val leagues: List<FifaLeagueResponse> = emptyList(),
        val success: Boolean = true
    )

    data class FifaPlayerResponse(
        val id: UUID,
        val leagueId: UUID?,
        val name: String
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
        val under: Double?
    )

    data class FifaOddRequest(
        val integrationId: Int,
        val odds: List<FifaSingleOddRequest>
    )

    data class FifaOddResponse(
        val success: Boolean = true
    )

    data class FifaRuleRequest(
        val id: UUID?,
        val type: FifaRuleTypes,
        val matchup: FifaMatchupTypes? = null,
        val scope: FifaStrategyScopeTypes? = null,
        val value: Double,
        val scopeValue: Int
    )

    data class FifaRuleResponse(
        val id: UUID?,
        val type: String,
        val matchup: String,
        val scope: String,
        val value: Double,
        val scopeValue: Double
    )

    data class FifaStrategyRequest(
        val id: UUID?,
        val name: String,
        val marketType: FifaMarketTypes,
        val marketSubTypes: List<FifaMarketSubTypes>,
        val leagues: List<UUID>,
        val excludedPlayers: List<UUID>,
        val rules: List<FifaRuleRequest>
    )

    data class FifaStrategyWriteResponse(
        val success: Boolean = true
    )

    data class FifaStrategyReadResponse(
        val id: UUID,
        val name: String,
        val marketType: FifaMarketTypes,
        val marketSubTypes: List<FifaMarketSubTypes>,
        val leagues: List<FifaLeagueResponse>,
        val excludedPlayers: List<FifaPlayerResponse>,
        val rules: List<FifaRuleResponse>
    )

    data class FifaStrategySingleResponse(
        val id: UUID,
        val name: String,
        val status: FifaStrategyStatus,
        val bets: Int,
        val result: Double,
        val roi: Double,
        val activeResult: Double,
        val activeRoi: Double
    )

    data class FifaStrategyListResponse(
        val strategies: List<FifaStrategySingleResponse> = emptyList(),
        val success: Boolean = true
    )

    data class FifaStrategyAgainstOddRequest(
        val strategy: FifaStrategy,
        val odds: FifaOddRequest
    )

    data class FifaStrategyAgainstOddResponse(
        val success: Boolean = true
    )

    data class FifaStrategyStatusRequest(
        val status: FifaStrategyStatus
    )

    data class FifaMarketTypeResponse(
        val marketType: FifaMarketTypes,
        val marketSubTypes: List<FifaMarketSubTypes>
    )

    data class FifaStrategyParamsResponse(
        val leagues: List<FifaLeagueResponse>,
        val marketTypes: List<FifaMarketTypeResponse>,
        val players: List<FifaPlayerResponse>,
        val ruleTypes: List<FifaRuleTypes>,
        val matchupTypes: List<FifaMatchupTypes>,
        val scopeTypes: List<FifaStrategyScopeTypes>
    )

    data class LastResultTimeResponse(
        val lastResultTime: Long? = null,
        val success: Boolean = true
    )
}

fun FifaLeague.toResponse(): FifaDTO.FifaLeagueResponse {
    return FifaDTO.FifaLeagueResponse(
        this.id, this.integrationId, this.name, this.link
    )
}

fun FifaPlayer.toResponse(): FifaDTO.FifaPlayerResponse {
    return FifaDTO.FifaPlayerResponse(
        this.id, this.league?.id, this.name
    )
}

fun FifaRule.toResponse(): FifaDTO.FifaRuleResponse {
    return FifaDTO.FifaRuleResponse(
        this.id,
        this.type.name,
        this.matchup?.name ?: "",
        this.scope?.name ?: "",
        this.value,
        this.scopeValue.toDouble()
    )
}
