package net.stakemetrics.application.entities

import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import java.util.Date
import java.util.UUID

data class FifaBet(
    val id: UUID = UUID.randomUUID(),
    var isPaperBet: Boolean = false,
    val strategy: FifaStrategy? = null,
    val match: FifaMatch? = null,
    val line: FifaMarketBetCandidates,
    val messages: MutableSet<Message> = mutableSetOf(),
    val handicap: Double? = null,
    val odds: Double,
    var status: BetStatusTypes = BetStatusTypes.PENDING,
    var profit: Double? = null,
    val betTime: Date = Date(),
    val oddSnapshotId: UUID
) {
    companion object {
        private const val HOURS_TO_CONSIDER_HANGING = 3
    }

    fun isHanging(): Boolean {
        val matchTime = match?.time ?: return false
        val sixHoursAgo = Date(System.currentTimeMillis() - HOURS_TO_CONSIDER_HANGING * 60 * 60 * 1000)
        return matchTime.before(sixHoursAgo) && status == BetStatusTypes.PENDING && profit == null
    }
}
