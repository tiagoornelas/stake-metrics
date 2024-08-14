package net.stakemetrics.application.entities

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaMarketTypes

data class FifaBet(
    val id: UUID = UUID.randomUUID(),
    val isPaperBet: Boolean = false,
    val strategy: FifaStrategy? = null,
    val match: FifaMatch? = null,
    val marketType: FifaMarketTypes,
    val line: FifaMarketBetCandidates,
    val scopes: MutableSet<FifaBetScope>,
    val messages: List<Message>,
    val handicap: Double? = null,
    val odds: Double,
    val status: BetStatusTypes = BetStatusTypes.PENDING,
    val profit: Double? = null,
    val oddOfferTime: Date,
    val betTime: Date
)
