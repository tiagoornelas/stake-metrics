package com.stakemetrics.backend.domain.enums.fifa

enum class FifaRuleTypes(val minValue: Double, maxValue: Double, val isPercentage: Boolean = false) {
    MINIMUM_ODDS(1.01, 100.0),
    MINIMUM_JUICE(0.01, 1.00, true),
    MINIMUM_PROBABILITY(0.0, 1.00, true)
}