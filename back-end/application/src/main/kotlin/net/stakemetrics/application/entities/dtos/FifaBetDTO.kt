package net.stakemetrics.application.entities.dtos

import java.util.Date
import net.stakemetrics.application.entities.FifaStrategy
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

}