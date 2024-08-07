package com.stakemetrics.backend.domain.services

import org.springframework.stereotype.Service

@Service
class OddAndLineCalculator {
    fun getProbability(odds: Double): Double {
        return 1 / odds
    }

    fun getFairLine(odds: Double): Double {
        return 1 / odds
    }

    fun getScoreThreshold(handicapLine: Double): Double {
        val fractionalPart = handicapLine % 1.0
        return when {
            fractionalPart == 0.25 || fractionalPart == 0.75 -> handicapLine - fractionalPart + 0.5
            else -> handicapLine
        }
    }

    fun getBettorsJuice(givenOdds: Double, fairLine: Double): Double {
        return (fairLine / givenOdds) - 1
    }
}