package com.stakemetrics.backend.plugins.http.dto

import com.stakemetrics.backend.domain.entities.fifa.FifaLeague
import com.stakemetrics.backend.domain.entities.fifa.FifaPlayer
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyRule
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyScope
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

    data class FifaStrategyRuleRequest(
        val id: UUID?,
        val type: FifaRuleTypes,
        val value: Double,
    )

    data class FifaStrategyRuleResponse(
        val id: UUID,
        val type: FifaRuleTypes,
        val value: Double,
    )

    data class FifaStrategyScopeRequest(
        val id: UUID?,
        val matchup: FifaMatchupTypes? = null,
        val type: FifaStrategyScopeTypes? = null,
        val value: Int,
        val rules: List<FifaStrategyRuleRequest>
    )

    data class FifaStrategyScopeResponse(
        val id: UUID,
        val matchup: FifaMatchupTypes? = null,
        val type: FifaStrategyScopeTypes? = null,
        val value: Int,
        val rules: List<FifaStrategyRuleResponse>
    )

    data class FifaStrategyRequest(
        val id: UUID?,
        val name: String,
        val marketType: FifaMarketTypes,
        val marketSubTypes: List<FifaMarketSubTypes>,
        val leagues: List<UUID>,
        val excludedPlayers: List<UUID>,
        val scopes: List<FifaStrategyScopeRequest>
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
        val scopes: List<FifaStrategyScopeResponse>
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

fun FifaStrategyScope.toResponse(): FifaDTO.FifaStrategyScopeResponse {
    return FifaDTO.FifaStrategyScopeResponse(
        this.id,
        this.matchup,
        this.type,
        this.value,
        this.rules.map { it.toResponse() }
    )
}

fun FifaStrategyRule.toResponse(): FifaDTO.FifaStrategyRuleResponse {
    return FifaDTO.FifaStrategyRuleResponse(
        this.id,
        this.type,
        this.value,
    )
}