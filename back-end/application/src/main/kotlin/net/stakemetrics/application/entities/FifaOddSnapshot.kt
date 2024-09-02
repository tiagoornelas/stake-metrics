package net.stakemetrics.application.entities

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.OddSnapshotTypes

data class FifaOddSnapshot(
    val id: UUID = UUID.randomUUID(),
    val fifaMatch: FifaMatch,
    val value: FifaDataSourceDTO.FifaGenericOddRequest,
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