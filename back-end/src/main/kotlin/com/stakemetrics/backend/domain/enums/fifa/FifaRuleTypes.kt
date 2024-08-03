package com.stakemetrics.backend.domain.enums.fifa

import com.stakemetrics.backend.domain.enums.RuleValueFormatTypes

enum class FifaRuleTypes(val minValue: Double, val maxValue: Double, val format: RuleValueFormatTypes) {
    MINIMUM_ODDS(1.01, 100.0, RuleValueFormatTypes.ODD),
    MINIMUM_JUICE(0.01, 1.00, RuleValueFormatTypes.PERCENTAGE),
    MINIMUM_PROBABILITY(0.0, 1.00, RuleValueFormatTypes.PERCENTAGE)
}