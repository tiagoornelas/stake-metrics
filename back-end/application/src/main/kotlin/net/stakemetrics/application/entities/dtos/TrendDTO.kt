package net.stakemetrics.application.entities.dtos

import net.stakemetrics.application.entities.FifaLeague

class TrendDTO {

    data class FifaGoalsLeagueTrendResponse(
        val league: FifaLeague,
        val trend: List<Int>
    )

    data class FifaGoalsTrendResponse(
        val leagueTrends: List<FifaGoalsLeagueTrendResponse>
    )

}