package net.stakemetrics.application.entities

import net.stakemetrics.application.annotations.SimilarityCheck
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import java.util.UUID

data class FifaStrategy(
    val id: UUID = UUID.randomUUID(),
    var status: FifaStrategyStatus = FifaStrategyStatus.INACTIVE,
    val name: String,
    @SimilarityCheck
    val marketType: FifaMarketTypes,
    @SimilarityCheck
    val marketSubTypes: MutableSet<FifaMarketSubTypes> = mutableSetOf(),
    @SimilarityCheck
    val leagues: MutableSet<FifaLeague> = mutableSetOf(),
    @SimilarityCheck
    val excludedPlayers: MutableSet<FifaPlayer> = mutableSetOf(),
    @SimilarityCheck
    val scopes: MutableSet<FifaStrategyScope> = mutableSetOf(),
    @SimilarityCheck
    val user: User?
) {
    val isPaperBetting: Boolean = status == FifaStrategyStatus.PAPER_BET
}
