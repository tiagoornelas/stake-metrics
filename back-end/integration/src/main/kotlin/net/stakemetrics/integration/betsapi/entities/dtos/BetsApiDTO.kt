package net.stakemetrics.integration.betsapi.entities.dtos

import BetsApiSoccerMatchStatus
import net.stakemetrics.integration.betsapi.entities.enums.MarketType

class BetsApiDTO {

    data class Pager(
        val page: Int,
        val per_page: Int,
        val total: Int
    )

    data class League(
        val id: String,
        val name: String,
        val cc: String?
    )

    data class Team(
        val id: String,
        val name: String,
        val image_id: String?,
        val cc: String?
    )

    data class Score(
        val home: String,
        val away: String
    )

    data class Stats(
        val corners: List<String>? = emptyList(),
        val goals: List<String>? = emptyList(),
        val penalties: List<String>? = emptyList(),
        val redcards: List<String>? = emptyList(),
        val substitutions: List<String>? = emptyList(),
        val yellowcards: List<String>? = emptyList(),
        val matching_dir: Int? = null,
        val odds_update: Map<MarketType, Long> = emptyMap()
    )

    data class MatchResponse(
        val success: Int,
        val pager: Pager,
        val results: List<Result>
    ) {
        data class Result(
            val id: String,
            val sport_id: String,
            val time: String,
            val time_status: BetsApiSoccerMatchStatus,
            val league: League,
            val home: Team,
            val away: Team,
            val ss: String?,
            val bet365Id: String? = null,
            val scores: Map<String, Score>? = emptyMap(),
            val stats: Stats? = null
        )
    }

    data class EventOddsResponse(
        val stats: Stats,
        val odds: Map<MarketType, List<Odds>> = emptyMap()
    ) {
        data class Odds(
            val id: String,
            val home_od: String? = null,
            val draw_od: String? = null,
            val away_od: String? = null,
            val over_od: String? = null,
            val under_od: String? = null,
            val handicap: String? = null,
            val ss: String? = null,
            val time_str: String? = null,
            val add_time: String
        )
    }
}
