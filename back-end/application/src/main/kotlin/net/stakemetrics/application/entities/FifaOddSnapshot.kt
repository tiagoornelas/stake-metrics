package net.stakemetrics.application.entities

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.OddSnapshotTypes

data class FifaOddSnapshot(
    val id: UUID = UUID.randomUUID(),
    val fifaMatch: FifaMatch,
    val goalsHandicap: Double? = null,
    val overGoalsOdd: Double? = null,
    val underGoalsOdd: Double? = null,
    val homeOdd: Double? = null,
    val drawOdd: Double? = null,
    val awayOdd: Double? = null,
    val trendScopeAnalysis: MutableSet<FifaTrendScopeAnalysis>,
    var status: OddSnapshotTypes = OddSnapshotTypes.PENDING,
    var homeProfit: Double? = null,
    var drawProfit: Double? = null,
    var awayProfit: Double? = null,
    var overProfit: Double? = null,
    var underProfit: Double? = null,
    var matchOddsWinnerSubType: FifaMarketSubTypes? = null,
    var goalLineWinnerSubType: FifaMarketSubTypes? = null,
    val createdAt: Date = Date()
)