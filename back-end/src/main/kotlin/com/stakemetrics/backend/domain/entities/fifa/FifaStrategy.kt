package com.stakemetrics.backend.domain.entities.fifa

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.enums.fifa.FifaMarketSubTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaMarketTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyStatus
import java.util.UUID

data class FifaStrategy(
    val id: UUID = UUID.randomUUID(),
    var status: FifaStrategyStatus = FifaStrategyStatus.PAPER_BET,
    val name: String,
    val marketType: FifaMarketTypes,
    val marketSubTypes: MutableSet<FifaMarketSubTypes> = mutableSetOf(),
    val leagues: MutableSet<FifaLeague> = mutableSetOf(),
    val excludedPlayers: MutableSet<FifaPlayer> = mutableSetOf(),
    val rules: MutableSet<FifaRule> = mutableSetOf(),
    val user: User?
)
