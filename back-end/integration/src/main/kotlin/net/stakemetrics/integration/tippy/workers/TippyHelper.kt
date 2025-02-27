package net.stakemetrics.integration.tippy.workers

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.integration.tippy.dto.TippyDTO
import net.stakemetrics.integration.tippy.enums.TippyLeagues
import net.stakemetrics.integration.tippy.enums.TippyMarketsTypes
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class TippyHelper {

    fun getMarketFromLine(line: FifaMarketBetCandidates): FifaMarketTypes? {
        return FifaMarketTypes.entries.find { marketType -> marketType.subTypes.any { it.betCandidates.contains(line) } }
    }

    fun getBetMatch(fifaMatch: FifaMatch, response: TippyDTO.Response): TippyDTO.EventResponse? {
        return response.events.find { it.fixtureId == fifaMatch.bet365Id.toString() }
    }

    fun getSelectionForBet(match: TippyDTO.EventResponse, fifaBet: FifaBet): TippyDTO.Selection {
        val market = getMarketFromLine(fifaBet.line)
        val selections = match.markets.first().selections

        assureMarketForSelection(market, selections)

        return when (market) {
            FifaMarketTypes.MATCH_ODDS -> getMatchOddsSelectionForBet(selections, fifaBet)
            FifaMarketTypes.ASIAN_GOAL_LINE -> getAsianGoalLineSelectionForBet(selections, fifaBet)
            else -> throw IllegalArgumentException("Invalid FifaMarketType")
        }
    }

    private fun assureMarketForSelection(market: FifaMarketTypes?, selections: List<TippyDTO.Selection>) {
        val tippyMarketsType = TippyMarketsTypes.entries.find { it.applicationType == market }
        val allMatch = selections.all { it.marketName == tippyMarketsType?.integrationName }
        require(allMatch) { "Market not found for selection" }
    }

    private fun getMatchOddsSelectionForBet(
        selections: List<TippyDTO.Selection>,
        fifaBet: FifaBet
    ): TippyDTO.Selection {
        return selections.find { it.name == fifaBet.line.name.lowercase(Locale.getDefault()) }
            ?: throw IllegalArgumentException("Selection not found")
    }

    fun getAsianGoalLineSelectionForBet(selections: List<TippyDTO.Selection>, fifaBet: FifaBet): TippyDTO.Selection {
        val selection = when (fifaBet.line) {
            FifaMarketBetCandidates.ASIAN_OVER_GOALS -> findSelectionByHeader(selections, "O")
            FifaMarketBetCandidates.ASIAN_UNDER_GOALS -> findSelectionByHeader(selections, "U")
            else -> null
        }

        return selection ?: throw IllegalArgumentException("No selection found for ${fifaBet.line} market type")
    }

    private fun findSelectionByHeader(selections: List<TippyDTO.Selection>, prefix: String): TippyDTO.Selection? =
        selections.find { it.header.split(" ").first() == prefix }

    fun getTippyLeagueFromStakeMetricsLeague(fifaLeague: FifaLeague): TippyLeagues {
        return TippyLeagues.entries.find { it.applicationType.nickname == fifaLeague.name }
            ?: throw IllegalArgumentException("Invalid FifaLeague when trying to convert to TippyLeague")
    }

    fun getTippyMarketFromStakeMetricsMarket(fifaMarket: FifaMarketTypes): TippyMarketsTypes {
        return TippyMarketsTypes.entries.find { it.applicationType == fifaMarket }
            ?: throw IllegalArgumentException("Invalid FifaMarket when trying to convert to TippyMarket")
    }

}