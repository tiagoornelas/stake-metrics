package net.stakemetrics.integration.tippy

import net.stakemetrics.application.entities.dtos.FifaBetDTO
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

    private fun buildRequest(payload: FifaBetDTO.AutoBetRequest): TippyDTO.AutoBetRequest {
        val fifaBet = payload.fifaBet
        val autoBettor = payload.autoBettor
        val marketType = tippyHelper.getMarketFromLine(fifaBet.line)

        requireNotNull(marketType) { "Market type not found for line ${fifaBet.line}" }
        requireNotNull(fifaBet.match?.integrationId) { "Match integrationId not found" }

        val matches = tippyApiRequester.getMatchesForLeagueAndMarket(fifaBet.match?.league!!, marketType)
        val match = tippyHelper.getBetMatch(fifaBet.match!!, matches)

        requireNotNull(match) { "Match not found for integrationId ${fifaBet.match?.integrationId}" }

        val selection = tippyHelper.getSelectionForBet(match, fifaBet)

        return TippyDTO.AutoBetRequest(
            integrationId = autoBettor.integrationId!!,
            selectionId = selection.id,
            fixtureId = fifaBet.match?.bet365Id!!.toString(),
            participantId = selection.participantId,
            odds = selection.odds,
            oddsFraction = selection.oddsFraction,
        )
    }

}