package com.stakemetrics.backend.domain.services

import org.springframework.stereotype.Service

@Service
class OddAndLineCalculator {
    fun getProbability(odds: Double): Double {
        return 1 / odds
    }

    fun getPointsThreshold(handicapLine: Double): Int {
        // TODO
        return 3
    }

    fun getBettorsJuice(givenOdds: Double, calculatedOdds: Double): Double {
        return givenOdds / calculatedOdds
    }
}