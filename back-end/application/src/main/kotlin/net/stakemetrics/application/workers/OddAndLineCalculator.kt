package net.stakemetrics.application.workers

import net.stakemetrics.application.utils.MathHelper
import org.springframework.stereotype.Service

@Service
class OddAndLineCalculator(private val mathHelper: MathHelper) {

    fun getFairLine(probability: Double): Double {
        return mathHelper.safeDivide(1.0, probability)
    }

    fun getScoreThreshold(handicapLine: Double): Double {
        val fractionalPart = handicapLine % 1.0
        return when {
            fractionalPart == 0.25 || fractionalPart == 0.75 -> handicapLine - fractionalPart + 0.5
            else -> handicapLine
        }
    }

    fun getBettorsJuice(givenOdds: Double, fairLine: Double): Double {
        return mathHelper.safeDivide(givenOdds, fairLine) - 1
    }
}