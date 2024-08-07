package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.domain.entities.fifa.FifaMatch
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyRule
import com.stakemetrics.backend.domain.enums.fifa.FifaMarketSubTypes
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import org.springframework.stereotype.Component

@Component
class FifaMatchOddsTipster : FifaTipster {
    override fun tip(
        marketSubType: FifaMarketSubTypes,
        rules: MutableSet<FifaStrategyRule>,
        odds: List<FifaDTO.FifaGenericOddRequest>,
        results: MutableSet<FifaMatch>
    ) {
        val matchOddsLine = odds.filter { it.isMatchOdds() }.maxByOrNull { it.updateTime }!!.toFifaMatchOddsLine()
        println(matchOddsLine)

//        rules.forEach { rule -> checkRule(marketSubType, rule, goalLine, results) }
//        fifaStrategyBettor.bet()
    }
}