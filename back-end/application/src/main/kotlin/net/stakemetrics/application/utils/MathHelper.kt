package net.stakemetrics.application.utils

import org.springframework.stereotype.Component

@Component
class MathHelper {
    fun safeDivide(numerator: Double, denominator: Double): Double {
        return if (denominator == 0.0) 0.0 else numerator / denominator
    }

    fun safeDivide(numerator: Int, denominator: Int): Double {
        return if (denominator.toDouble() == 0.0) 0.0 else numerator.toDouble() / denominator.toDouble()
    }
}