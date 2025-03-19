package net.stakemetrics.application.entities.dtos

import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.FifaStrategyScope
import net.stakemetrics.application.entities.GenericScope
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.enums.FifaRuleTypes
import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import net.stakemetrics.application.entities.enums.MatchupTypes
import net.stakemetrics.application.entities.enums.RuleValueFormatTypes
import net.stakemetrics.application.entities.enums.StrategyScopeTypes
import java.util.Date
import java.util.UUID

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
        val todaysResult: Double,
        val averageDailyBets: Int
    )

    data class FifaStrategyListResponse(
        val strategies: List<FifaStrategyStatisticSingleResponse> = emptyList(),
        val success: Boolean = true
    )

    data class FifaStrategyAgainstOddRequest(
        val strategy: FifaStrategy,
        val oddSnapshot: FifaOddSnapshot
    )

    data class GenericScopePastResults(
        val scope: GenericScope? = null,
        val pastResults: MutableSet<FifaMatch> = mutableSetOf()
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

    data class FifaMatchQuickIdentifier(
        val home: FifaPlayer? = null,
        val away: FifaPlayer? = null,
        val league: FifaLeague? = null
    )

    data class CumulativeProfitResponse(
        val id: UUID,
        val cumulativeProfit: List<Double>
    )

    data class SimpleReportBet(
        val betTime: Date? = null,
        val matchTime: Date? = null,
        val leagueName: String? = null,
        val homeName: String? = null,
        val awayName: String? = null,
        val homeScore: Int = 0,
        val awayScore: Int = 0,
        val totalScore: Int = 0,
        val line: String? = null,
        val handicap: Double? = null,
        val odds: Double? = null,
        val status: BetStatusTypes? = null,
        val profit: Double? = null
    )

    data class SimpleReportResponse(
        val bets: List<SimpleReportBet>
    )

    data class TrendScopeAnalysisDTO(
        val matchup: MatchupTypes?,
        val type: StrategyScopeTypes?,
        val totalMatches: Int?,
        val homePlayerProbability: Double?,
        val homePlayerFairLine: Double?,
        val homePlayerJuice: Double?,
        val drawProbability: Double?,
        val drawFairLine: Double?,
        val drawJuice: Double?,
        val awayPlayerProbability: Double?,
        val awayPlayerFairLine: Double?,
        val awayPlayerJuice: Double?,
        val overProbability: Double?,
        val overFairLine: Double?,
        val overJuice: Double?,
        val underProbability: Double?,
        val underFairLine: Double?,
        val underJuice: Double?
    )

    data class DetailedReportBet(
        val betTime: Date? = null,
        val matchTime: Date? = null,
        val leagueName: String? = null,
        val homeName: String? = null,
        val awayName: String? = null,
        val homeScore: Int = 0,
        val awayScore: Int = 0,
        val totalScore: Int = 0,
        val line: String? = null,
        val handicap: Double? = null,
        val odds: Double? = null,
        val status: BetStatusTypes? = null,
        val profit: Double? = null,
        val trendScopeAnalysis: List<TrendScopeAnalysisDTO>
    )

    data class DetailedReportResponse(
        val bets: List<DetailedReportBet>
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