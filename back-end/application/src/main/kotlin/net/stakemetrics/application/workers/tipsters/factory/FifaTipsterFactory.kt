package net.stakemetrics.application.workers.tipsters.factory

import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.workers.tipsters.FifaGoalLineTipster
import net.stakemetrics.application.workers.tipsters.FifaMatchOddsTipster
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