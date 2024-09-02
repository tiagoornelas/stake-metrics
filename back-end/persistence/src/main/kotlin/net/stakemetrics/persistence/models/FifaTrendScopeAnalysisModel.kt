package net.stakemetrics.persistence.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import net.stakemetrics.application.entities.FifaTrendScopeAnalysis
import net.stakemetrics.application.entities.enums.MatchupTypes
import net.stakemetrics.application.entities.enums.StrategyScopeTypes

@Entity
@Table(name = "fifa_trend_scope_analysis")
data class FifaTrendScopeAnalysisModel(
    @Id
    val id: UUID? = null,
    val matchup: MatchupTypes? = null,
    val type: StrategyScopeTypes? = null,
    val totalMatches: Int? = null,
    val homePlayerProbability: Double? = null,
    val homePlayerFairLine: Double? = null,
    val homePlayerJuice: Double? = null,
    val drawProbability: Double? = null,
    val drawFairLine: Double? = null,
    val drawJuice: Double? = null,
    val awayPlayerProbability: Double? = null,
    val awayPlayerFairLine: Double? = null,
    val awayPlayerJuice: Double? = null,
    val overProbability: Double? = null,
    val overFairLine: Double? = null,
    val overJuice: Double? = null,
    val underProbability: Double? = null,
    val underFairLine: Double? = null,
    val underJuice: Double? = null
) {
    fun toDomain(): FifaTrendScopeAnalysis {
        return FifaTrendScopeAnalysis(
            id = id!!,
            matchup = matchup!!,
            type = type!!,
            totalMatches = totalMatches!!,
            homePlayerProbability = homePlayerProbability!!,
            homePlayerFairLine = homePlayerFairLine!!,
            homePlayerJuice = homePlayerJuice!!,
            drawProbability = drawProbability!!,
            drawFairLine = drawFairLine!!,
            drawJuice = drawJuice!!,
            awayPlayerProbability = awayPlayerProbability!!,
            awayPlayerFairLine = awayPlayerFairLine!!,
            awayPlayerJuice = awayPlayerJuice!!,
            overProbability = overProbability!!,
            overFairLine = overFairLine!!,
            overJuice = overJuice!!,
            underProbability = underProbability!!,
            underFairLine = underFairLine!!,
            underJuice = underJuice!!
        )
    }
}

fun FifaTrendScopeAnalysis.toModel(): FifaTrendScopeAnalysisModel {
    return FifaTrendScopeAnalysisModel(
        id = this.id,
        matchup = this.matchup,
        type = this.type,
        totalMatches = this.totalMatches,
        homePlayerProbability = this.homePlayerProbability,
        homePlayerFairLine = this.homePlayerFairLine,
        homePlayerJuice = this.homePlayerJuice,
        drawProbability = this.drawProbability,
        drawFairLine = this.drawFairLine,
        drawJuice = this.drawJuice,
        awayPlayerProbability = this.awayPlayerProbability,
        awayPlayerFairLine = this.awayPlayerFairLine,
        awayPlayerJuice = this.awayPlayerJuice,
        overProbability = this.overProbability,
        overFairLine = this.overFairLine,
        overJuice = this.overJuice,
        underProbability = this.underProbability,
        underFairLine = this.underFairLine,
        underJuice = this.underJuice
    )
}