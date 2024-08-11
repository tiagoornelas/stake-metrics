package net.stakemetrics.application.workers.tipsters.factory

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes

interface FifaTipster {
    fun tip(
        matchupPlayerNames: Pair<String, String>,
        marketSubType: FifaMarketSubTypes,
        rules: MutableSet<FifaStrategyRule>,
        odds: List<FifaDataSourceDTO.FifaGenericOddRequest>,
        results: MutableSet<FifaMatch>
    )
}