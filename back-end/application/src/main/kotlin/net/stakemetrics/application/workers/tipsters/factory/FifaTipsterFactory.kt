package net.stakemetrics.application.workers.tipsters.factory

import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.workers.tipsters.FifaGoalLineBetCloser
import net.stakemetrics.application.workers.tipsters.FifaGoalLineTipster
import net.stakemetrics.application.workers.tipsters.FifaMatchOddsBetCloser
import net.stakemetrics.application.workers.tipsters.FifaMatchOddsTipster
import org.springframework.stereotype.Component

@Component
class FifaTipsterFactory(
    private val goalLineTipster: FifaGoalLineTipster,
    private val matchOddsTipster: FifaMatchOddsTipster,
    private val goalLineBetCloser: FifaGoalLineBetCloser,
    private val matchOddsBetCloser: FifaMatchOddsBetCloser
) {
    fun getTipster(marketType: FifaMarketTypes): FifaTipster {
        return when (marketType) {
            FifaMarketTypes.GOAL_LINE -> goalLineTipster
            FifaMarketTypes.MATCH_ODDS -> matchOddsTipster
        }
    }

    fun getBetCloser(marketType: FifaMarketTypes): FifaBetCloser {
        return when (marketType) {
            FifaMarketTypes.GOAL_LINE -> goalLineBetCloser
            FifaMarketTypes.MATCH_ODDS -> matchOddsBetCloser
        }
    }
}