package net.stakemetrics.application.workers.tipsters.factory

import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategyRule

interface FifaTipster {
    fun tip(
        matchupPlayerNames: Pair<String, String>,
        marketSubType: FifaMarketSubTypes,
        rules: MutableSet<FifaStrategyRule>,
        odds: List<FifaDTO.FifaGenericOddRequest>,
        results: MutableSet<FifaMatch>
    )
}