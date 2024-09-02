package net.stakemetrics.application.workers.tipsters.factory

import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.FifaStrategyRule
import net.stakemetrics.application.entities.FifaTrendScopeAnalysis
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates

interface FifaTipster {

    fun analyze(
        betCandidate: FifaMarketBetCandidates,
        rules: MutableSet<FifaStrategyRule>,
        odds: FifaOddSnapshot,
        analysis: FifaTrendScopeAnalysis
    )

}