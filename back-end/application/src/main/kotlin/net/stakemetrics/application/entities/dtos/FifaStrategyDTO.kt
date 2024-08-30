package net.stakemetrics.application.entities.dtos

import java.util.UUID
import net.stakemetrics.application.entities.GenericScope
import net.stakemetrics.application.entities.*
import net.stakemetrics.application.entities.enums.*

class FifaStrategyDTO {

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
        val matchup: MatchupTypes? = null,
        val type: StrategyScopeTypes? = null,
        val rules: List<FifaStrategyRuleRequest>
    )

    data class FifaStrategyScopeResponse(
        val id: UUID,
        val matchup: MatchupTypes? = null,
        val type: StrategyScopeTypes? = null,
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
        val leagues: List<FifaLeagueDTO.FifaLeagueResponse>,
        val excludedPlayers: List<FifaPlayerDTO.FifaPlayerResponse>,
        val scopes: List<FifaStrategyScopeResponse>
    )

    data class FifaStrategyStatisticSingleResponse(
        val id: UUID,
        val name: String,
        val status: FifaStrategyStatus,
        val openBets: Int,
        val bets: Int,
        val result: Double,
        val roi: Double,
        val activeResult: Double,
        val activeRoi: Double
    )

    data class FifaStrategyListResponse(
        val strategies: List<FifaStrategyStatisticSingleResponse> = emptyList(),
        val success: Boolean = true
    )

    data class FifaStrategyAgainstOddRequest(
        val strategy: FifaStrategy,
        val odds: FifaDataSourceDTO.FifaOddRequest
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
        val leagues: List<FifaLeagueDTO.FifaLeagueResponse>,
        val marketTypes: List<FifaMarketTypeResponse>,
        val players: List<FifaPlayerDTO.FifaPlayerResponse>,
        val ruleTypes: List<FifaRuleTypesResponse>,
        val matchupTypes: List<MatchupTypes>,
        val scopeTypes: List<StrategyScopeTypes>
    )

    data class GenericScopePastResults(
        val scope: GenericScope? = null,
        val pastResults: MutableSet<FifaMatch> = mutableSetOf()
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

fun FifaStrategyScope.toResponse(): FifaStrategyDTO.FifaStrategyScopeResponse {
    return FifaStrategyDTO.FifaStrategyScopeResponse(
        this.id,
        this.matchup,
        this.type,
        this.rules.map { it.toResponse() }
    )
}

fun FifaStrategyRule.toResponse(): FifaStrategyDTO.FifaStrategyRuleResponse {
    return FifaStrategyDTO.FifaStrategyRuleResponse(
        this.id,
        this.type,
        this.value,
    )
}