package net.stakemetrics.persistence.mappers

import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.persistence.jpa.FifaBetJpaRepository
import net.stakemetrics.persistence.jpa.FifaBetJpaRepository.DetailedReportRawProjection
import org.springframework.stereotype.Component

@Component
class DetailedReportMapper {
    fun toDetailedReportBets(rawProjections: List<DetailedReportRawProjection>): List<FifaStrategyDTO.DetailedReportBet> {
        return rawProjections
            .groupBy { it.id }
            .map { (_, projections) -> toDetailedReportBet(projections) }
    }

    private fun toDetailedReportBet(projections: List<DetailedReportRawProjection>): FifaStrategyDTO.DetailedReportBet {
        val first = projections.first()
        return FifaStrategyDTO.DetailedReportBet(
            betTime = first.betTime,
            matchTime = first.matchTime,
            leagueName = first.leagueName,
            homeName = first.homeName,
            awayName = first.awayName,
            homeScore = first.homeScore ?: 0,
            awayScore = first.awayScore ?: 0,
            line = first.line,
            handicap = first.handicap,
            odds = first.odds,
            status = first.status,
            profit = first.profit,
            trendScopeAnalysis = projections
                .mapNotNull { it.trendScopeAnalysis }
                .map { toTrendScopeAnalysisDTO(it) }
        )
    }

    private fun toTrendScopeAnalysisDTO(projection: FifaBetJpaRepository.TrendScopeAnalysisProjection): FifaStrategyDTO.TrendScopeAnalysisDTO {
        return FifaStrategyDTO.TrendScopeAnalysisDTO(
            matchup = projection.matchup,
            type = projection.type,
            totalMatches = projection.totalMatches,
            homePlayerProbability = projection.homePlayerProbability,
            homePlayerFairLine = projection.homePlayerFairLine,
            homePlayerJuice = projection.homePlayerJuice,
            drawProbability = projection.drawProbability,
            drawFairLine = projection.drawFairLine,
            drawJuice = projection.drawJuice,
            awayPlayerProbability = projection.awayPlayerProbability,
            awayPlayerFairLine = projection.awayPlayerFairLine,
            awayPlayerJuice = projection.awayPlayerJuice,
            overProbability = projection.overProbability,
            overFairLine = projection.overFairLine,
            overJuice = projection.overJuice,
            underProbability = projection.underProbability,
            underFairLine = projection.underFairLine,
            underJuice = projection.underJuice
        )
    }
}
