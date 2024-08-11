package net.stakemetrics.application.entities.dtos

import java.util.UUID
import net.stakemetrics.application.entities.*
import net.stakemetrics.application.entities.enums.*

class FifaDTO {

    data class FifaLeagueResponse(
        val id: UUID,
        val integrationId: Long,
        val name: String,
        val link: String
    )

    data class FifaPlayerResponse(
        val id: UUID,
        val leagueId: UUID?,
        val name: String
    )

    data class FifaMatchRequest(
        val integrationId: Long,
        val time: Long,
        val status: FifaMatchStatusTypes,
        val leagueId: Long,
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

    data class FifaGenericOddRequest(
        val marketType: FifaMarketTypes,
        val lastCheckedTime: Long?,
        val oddOfferTime: Long,
        val matchTime: Long,
        val handicap: Double?,
        val over: Double?,
        val under: Double?,
        val home: Double?,
        val draw: Double?,
        val away: Double?
    ) {

        fun isMatchOdds(): Boolean {
            return home != null && draw != null && away != null
        }

        fun toFifaMatchOddsLine(): FifaMatchOddsOddRequest {
            return FifaMatchOddsOddRequest(
                marketType,
                lastCheckedTime!!,
                oddOfferTime,
                matchTime,
                home!!,
                draw!!,
                away!!
            )
        }

        fun isGoalLine(): Boolean {
            return handicap != null && over != null && under != null
        }

        fun toFifaGoalLine(): FifaGoalLineOddRequest {
            return FifaGoalLineOddRequest(
                marketType,
                lastCheckedTime!!,
                oddOfferTime,
                matchTime,
                handicap!!,
                over!!,
                under!!
            )
        }
    }

    data class FifaGoalLineOddRequest(
        val marketType: FifaMarketTypes,
        val lastCheckedTime: Long,
        val oddOfferTime: Long,
        val matchTime: Long,
        val handicap: Double,
        val over: Double,
        val under: Double
    )

    data class FifaMatchOddsOddRequest(
        val marketType: FifaMarketTypes,
        val lastCheckedTime: Long,
        val oddOfferTime: Long,
        val matchTime: Long,
        val home: Double,
        val draw: Double,
        val away: Double
    )

    data class FifaOddRequest(
        val leagueIntegrationId: Long,
        val homePlayerName: String,
        val awayPlayerName: String,
        val odds: List<FifaGenericOddRequest>
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

    data class FifaRuleTypesResponse(
        val type: FifaRuleTypes,
        val minValue: Double,
        val maxValue: Double,
        val defaultValue: Double,
        val format: RuleValueFormatTypes
    )

    data class FifaStrategyParamsResponse(
        val leagues: List<FifaLeagueResponse>,
        val marketTypes: List<FifaMarketTypeResponse>,
        val players: List<FifaPlayerResponse>,
        val ruleTypes: List<FifaRuleTypesResponse>,
        val matchupTypes: List<FifaMatchupTypes>,
        val scopeTypes: List<FifaStrategyScopeTypes>
    )

    data class FifaStrategyScopePastResults(
        val scope: FifaStrategyScope? = null,
        val pastResults: MutableSet<FifaMatch> = mutableSetOf()
    )

    data class FifaMatchQuickIdentifier(
        val home: FifaPlayer? = null,
        val away: FifaPlayer? = null,
        val league: FifaLeague? = null
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