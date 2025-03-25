package net.stakemetrics.application.entities

import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import java.util.Date
import java.util.UUID

data class FifaMatch(
    val id: UUID = UUID.randomUUID(),
    val integrationId: Long,
    val bet365Id: Long? = null,
    val time: Date,
    val status: FifaMatchStatusTypes = FifaMatchStatusTypes.NOT_STARTED,
    val league: FifaLeague? = null,
    val home: FifaPlayer? = null,
    val away: FifaPlayer? = null,
    val homeGoalsAtHalfTime: Int? = null,
    val homeGoalsAtFullTime: Int? = null,
    val awayGoalsAtHalfTime: Int? = null,
    val awayGoalsAtFullTime: Int? = null,
    val totalGoalsAtHalfTime: Int? = null,
    val totalGoalsAtFullTime: Int? = null,
    val winner: FifaPlayer? = null
) {
    fun hasMatchAlreadyBegun(): Boolean {
        return time.before(Date())
    }
}