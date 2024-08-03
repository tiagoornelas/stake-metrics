package com.stakemetrics.backend.domain.enums.fifa

import com.stakemetrics.backend.domain.enums.RuleValueFormatTypes
import com.stakemetrics.backend.plugins.http.dto.FifaDTO

enum class FifaRuleTypes(
    val minValue: Double,
    val maxValue: Double,
    val defaultValue: Double,
    val format: RuleValueFormatTypes
) {
    MINIMUM_ODDS(1.01, 100.0, 2.00, RuleValueFormatTypes.ODD),
    MINIMUM_JUICE(0.01, 1.00, 0.10, RuleValueFormatTypes.PERCENTAGE),
    MINIMUM_PROBABILITY(0.0, 1.00, 0.50, RuleValueFormatTypes.PERCENTAGE)
}

fun FifaRuleTypes.toResponse() = FifaDTO.FifaRuleTypesResponse(this, minValue, maxValue, defaultValue, format)