package net.stakemetrics.application.entities

import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import java.util.UUID

data class FifaStrategy(
    val id: UUID = UUID.randomUUID(),
    var status: FifaStrategyStatus = FifaStrategyStatus.INACTIVE,
    val name: String,
    val marketType: FifaMarketTypes,
    val marketSubTypes: MutableSet<FifaMarketSubTypes> = mutableSetOf(),
    val leagues: MutableSet<FifaLeague> = mutableSetOf(),
    val excludedPlayers: MutableSet<FifaPlayer> = mutableSetOf(),
    val scopes: MutableSet<FifaStrategyScope> = mutableSetOf(),
    val user: User?
) {
    val isPaperBetting: Boolean = status == FifaStrategyStatus.PAPER_BET
}
