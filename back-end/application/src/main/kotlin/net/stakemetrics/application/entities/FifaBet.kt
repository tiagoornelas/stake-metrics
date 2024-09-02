package net.stakemetrics.application.entities

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates

data class FifaBet(
    val id: UUID = UUID.randomUUID(),
    val isPaperBet: Boolean = false,
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
)
