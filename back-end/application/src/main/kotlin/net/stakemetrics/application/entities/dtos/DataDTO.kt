package net.stakemetrics.application.entities.dtos

import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import java.util.UUID

class DataDTO {

    data class OddSnapshotResponse(
        val oddSnapshots: List<FifaOddSnapshot>
    )

    data class FifaStrategiesByLeaguePerformanceSingleResponse(
        val strategyId: UUID,
        val strategyName: String,
        val marketType: FifaMarketTypes,
        val leagueName: String,
        val leagueId: UUID,
        val userLogin: String,
        val profitLastTwoHours: Double,
        val profitLastFourHours: Double,
        val profitLastSixHours: Double,
        val profitLastTwelveHours: Double,
        val profitLastTwentyFourHours: Double,
        val profitLastWeek: Double
    )

    data class FifaStrategiesByLeaguePerformanceResponse(
        val strategies: List<FifaStrategiesByLeaguePerformanceSingleResponse>
    )

}