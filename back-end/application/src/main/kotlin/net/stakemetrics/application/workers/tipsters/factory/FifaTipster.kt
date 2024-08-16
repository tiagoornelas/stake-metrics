package net.stakemetrics.application.workers.tipsters.factory

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates

interface FifaTipster {
    fun getTipstersSpecificLines(
        odds: List<FifaDataSourceDTO.FifaGenericOddRequest>,
    ): FifaDataSourceDTO.FifaGenericOddRequest

    fun analyze(
        betCandidate: FifaMarketBetCandidates,
        matchupPlayerNames: Pair<String, String>,
        rules: MutableSet<FifaStrategyRule>,
        odds: FifaDataSourceDTO.FifaGenericOddRequest,
        results: MutableSet<FifaMatch>
    )
}