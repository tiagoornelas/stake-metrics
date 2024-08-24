package net.stakemetrics.application.entities.dtos

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates

class FifaBetDTO {

    data class BetRequest(
        val strategy: FifaStrategy,
        val leagueIntegrationId: Long,
        val homePlayerName: String,
        val awayPlayerName: String,
        val matchIntegrationId: Long,
        val matchTime: Date,
        val candidate: FifaMarketBetCandidates,
        val lineOdds: FifaDataSourceDTO.FifaGenericOddRequest
    )

    data class BetResponse(
        val id: UUID,
        val isPaperBet: Boolean,
        val strategyName: String,
        val leagueName: String,
        val homePlayerName: String,
        val awayPlayerName: String,
        val matchTime: Date,
        val betTime: Date,
        val candidate: FifaMarketBetCandidates,
        val odds: Double,
        val status: BetStatusTypes,
        val handicap: Double?,
        val profit: Double?,
    )

    data class DeleteResponse(
        val success: Boolean = true
    )

    data class CloseBetRequest(
        val bet: FifaBet,
    )

}

fun FifaBet.toResponse(): FifaBetDTO.BetResponse {
    return FifaBetDTO.BetResponse(
        id = this.id,
        isPaperBet = this.isPaperBet,
        strategyName = this.strategy?.name ?: "",
        leagueName = this.match?.league?.name ?: "",
        homePlayerName = this.match?.home?.name ?: "",
        awayPlayerName = this.match?.away?.name ?: "",
        matchTime = this.match?.time ?: Date(),
        betTime = this.betTime,
        candidate = this.line,
        odds = this.odds,
        status = this.status,
        handicap = this.handicap,
        profit = this.profit
    )
}