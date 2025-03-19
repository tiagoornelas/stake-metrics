package net.stakemetrics.integration.tippy

import net.stakemetrics.application.entities.AutoBettor
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.exceptions.IntegrationException
import net.stakemetrics.application.service.IAutoBettorService
import net.stakemetrics.integration.tippy.dto.TippyDTO
import net.stakemetrics.integration.tippy.workers.TippyApiRequester
import net.stakemetrics.integration.tippy.workers.TippyHelper
import org.springframework.stereotype.Service

@Service
class TippyAutoBettorService(private val tippyApiRequester: TippyApiRequester, private val tippyHelper: TippyHelper) :
    IAutoBettorService {

    override fun bet(payload: FifaBetDTO.AutoBetRequest) {
        val request = buildRequest(payload)
        tippyApiRequester.autoBet(request)
    }

    private fun buildRequest(payload: FifaBetDTO.AutoBetRequest): TippyDTO.AutoBetRequestWithIntegrationInfo {
        val fifaBet = payload.fifaBet
        val autoBettor = payload.autoBettor
        val marketType = tippyHelper.getMarketFromLine(fifaBet.line)

        requireNotNull(marketType) { "Market type not found for line ${fifaBet.line}" }
        requireNotNull(fifaBet.match?.integrationId) { "Match integrationId not found" }

        val matches = tippyApiRequester.getMatchesForLeagueAndMarket(fifaBet.match?.league!!, marketType)
        val match = tippyHelper.getBetMatch(fifaBet.match!!, matches)

        requireNotNull(match) { "Match not found for integrationId ${fifaBet.match?.integrationId}" }

        val selection = tippyHelper.getSelectionForBet(match, fifaBet)

        return TippyDTO.AutoBetRequestWithIntegrationInfo(
            integrationId = autoBettor.integrationId!!,
            selectionId = selection.id
        )
    }

    override fun checkIntegration(autoBettor: AutoBettor): FifaBetDTO.AutoBettorIntegrationResponse {
        return try {
            val response = tippyApiRequester.checkIntegration(autoBettor.integrationId!!)
            if (!response.channel.isAdmin) throw IntegrationException("Token does not belong to an admin for channel ${response.channel.name}")
            FifaBetDTO.AutoBettorIntegrationResponse(
                success = true,
                channelName = response.channel.name
            )
        } catch (e: Exception) {
            FifaBetDTO.AutoBettorIntegrationResponse(
                success = false,
                channelName = null
            )
        }
    }
}