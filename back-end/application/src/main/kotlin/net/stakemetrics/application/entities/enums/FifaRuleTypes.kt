package net.stakemetrics.application.entities.enums

import net.stakemetrics.application.entities.dtos.FifaStrategyDTO

enum class FifaRuleTypes(
    val minValue: Double,
    val maxValue: Double,
    val defaultValue: Double,
    val format: RuleValueFormatTypes
) {
    MINIMUM_ODDS(1.01, 100.0, 2.00, RuleValueFormatTypes.ODD),
    MINIMUM_JUICE(0.01, 1.00, 0.10, RuleValueFormatTypes.PERCENTAGE),
    MINIMUM_PROBABILITY(0.0, 1.00, 0.50, RuleValueFormatTypes.PERCENTAGE),
    MINIMUM_MATCHES(3.0, 3000.0, 10.0, RuleValueFormatTypes.INTEGER),
}

fun FifaRuleTypes.toResponse() = FifaStrategyDTO.FifaRuleTypesResponse(this, minValue, maxValue, defaultValue, format)