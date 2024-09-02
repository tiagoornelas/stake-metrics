package net.stakemetrics.application.entities

import java.util.UUID
import net.stakemetrics.application.entities.enums.MatchupTypes
import net.stakemetrics.application.entities.enums.StrategyScopeTypes

data class FifaTrendScopeAnalysis(
    val id: UUID = UUID.randomUUID(),
    override val matchup: MatchupTypes,
    override val type: StrategyScopeTypes,
    val totalMatches: Int,
    val homePlayerProbability: Double,
    val homePlayerFairLine: Double,
    val homePlayerJuice: Double,
    val drawProbability: Double,
    val drawFairLine: Double,
    val drawJuice: Double,
    val awayPlayerProbability: Double,
    val awayPlayerFairLine: Double,
    val awayPlayerJuice: Double,
    val overProbability: Double,
    val overFairLine: Double,
    val overJuice: Double,
    val underProbability: Double,
    val underFairLine: Double,
    val underJuice: Double
) : StrategyScope