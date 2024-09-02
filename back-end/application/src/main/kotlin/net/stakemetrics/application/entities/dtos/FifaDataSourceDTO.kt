package net.stakemetrics.application.entities.dtos

import java.util.Date
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
        val matchTime: Date? = null,
        val goalsHandicap: Double? = null,
        val overGoals: Double? = null,
        val underGoals: Double? = null,
        val home: Double? = null,
        val draw: Double? = null,
        val away: Double? = null
    )

    data class FifaOddRequest(
        val leagueIntegrationId: Long,
        val matchIntegrationId: Long,
        val homePlayerName: String,
        val awayPlayerName: String,
        val odds: FifaGenericOddRequest
    )

}