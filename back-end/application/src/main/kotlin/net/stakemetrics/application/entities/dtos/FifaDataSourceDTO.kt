package net.stakemetrics.application.entities.dtos

import java.util.Date
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes

class FifaDataSourceDTO {

    data class FifaMatchRequest(
        val integrationId: Long,
        val time: Date,
        val status: FifaMatchStatusTypes,
        val leagueId: Long,
        val home: String,
        val away: String,
        val homeGoalsAtHalfTime: Int? = null,
        val homeGoalsAtFullTime: Int? = null,
        val awayGoalsAtHalfTime: Int? = null,
        val awayGoalsAtFullTime: Int? = null,
        val totalGoalsAtHalfTime: Int? = null,
        val totalGoalsAtFullTime: Int? = null,
        val winner: String? = null
    )

    data class FifaGenericOddRequest(
        val marketType: FifaMarketTypes,
        val lastCheckedTime: Date?,
        val oddOfferTime: Date,
        val matchTime: Date,
        val handicap: Double?,
        val over: Double?,
        val under: Double?,
        val home: Double?,
        val draw: Double?,
        val away: Double?
    ) {

        fun isMatchOdds(): Boolean {
            return home != null && draw != null && away != null
        }

        fun toFifaMatchOddsLine(): FifaMatchOddsOddRequest {
            return FifaMatchOddsOddRequest(
                marketType,
                lastCheckedTime!!,
                oddOfferTime,
                matchTime,
                home!!,
                draw!!,
                away!!
            )
        }

        fun isGoalLine(): Boolean {
            return handicap != null && over != null && under != null
        }

        fun toFifaGoalLine(): FifaGoalLineOddRequest {
            return FifaGoalLineOddRequest(
                marketType,
                lastCheckedTime!!,
                oddOfferTime,
                matchTime,
                handicap!!,
                over!!,
                under!!
            )
        }
    }

    data class FifaGoalLineOddRequest(
        val marketType: FifaMarketTypes,
        val lastCheckedTime: Date,
        val oddOfferTime: Date,
        val matchTime: Date,
        val handicap: Double,
        val over: Double,
        val under: Double
    )

    data class FifaMatchOddsOddRequest(
        val marketType: FifaMarketTypes,
        val lastCheckedTime: Date,
        val oddOfferTime: Date,
        val matchTime: Date,
        val home: Double,
        val draw: Double,
        val away: Double
    )

    data class FifaOddRequest(
        val leagueIntegrationId: Long,
        val homePlayerName: String,
        val awayPlayerName: String,
        val odds: List<FifaGenericOddRequest>
    )

}