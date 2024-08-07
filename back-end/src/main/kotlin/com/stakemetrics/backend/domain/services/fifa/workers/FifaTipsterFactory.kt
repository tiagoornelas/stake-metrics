package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.domain.enums.fifa.FifaMarketTypes
import org.springframework.stereotype.Component

@Component
class FifaTipsterFactory(
    private val goalLineTipster: FifaGoalLineTipster,
    private val matchOddsTipster: FifaMatchOddsTipster
) {
    fun getTipster(marketType: FifaMarketTypes): FifaTipster {
        return when (marketType) {
            FifaMarketTypes.GOAL_LINE -> goalLineTipster
            FifaMarketTypes.MATCH_ODDS -> matchOddsTipster
        }
    }
}