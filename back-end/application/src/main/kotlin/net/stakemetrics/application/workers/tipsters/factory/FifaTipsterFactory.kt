package net.stakemetrics.application.workers.tipsters.factory

import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.workers.tipsters.FifaAsianGoalLineBetCloser
import net.stakemetrics.application.workers.tipsters.FifaAsianGoalLineTipster
import net.stakemetrics.application.workers.tipsters.FifaMatchOddsBetCloser
import net.stakemetrics.application.workers.tipsters.FifaMatchOddsTipster
import org.springframework.stereotype.Component

@Component
class FifaTipsterFactory(
    private val asianGoalLineTipster: FifaAsianGoalLineTipster,
    private val matchOddsTipster: FifaMatchOddsTipster,
    private val asianGoalLineBetCloser: FifaAsianGoalLineBetCloser,
    private val matchOddsBetCloser: FifaMatchOddsBetCloser
) {
    fun getTipster(marketType: FifaMarketTypes): FifaTipster {
        return when (marketType) {
            FifaMarketTypes.ASIAN_GOAL_LINE -> asianGoalLineTipster
            FifaMarketTypes.MATCH_ODDS -> matchOddsTipster
        }
    }

    fun getBetCloser(marketType: FifaMarketTypes): FifaBetCloser {
        return when (marketType) {
            FifaMarketTypes.ASIAN_GOAL_LINE -> asianGoalLineBetCloser
            FifaMarketTypes.MATCH_ODDS -> matchOddsBetCloser
        }
    }
}