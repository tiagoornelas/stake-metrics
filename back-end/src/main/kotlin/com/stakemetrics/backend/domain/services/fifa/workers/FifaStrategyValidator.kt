package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.domain.enums.fifa.FifaRuleTypes
import com.stakemetrics.backend.plugins.http.dto.FifaDTO

class FifaStrategyValidator {
    fun validate(dto: FifaDTO.FifaStrategyRequest) {
        if (dto.name.isBlank() || dto.marketSubTypes.isEmpty() || dto.leagues.isEmpty()) {
            throw IllegalArgumentException("Name, marketSubTypes and leagues cannot be blank")
        }

        if (dto.scopes.isEmpty()) {
            throw IllegalArgumentException("Scopes cannot be empty")
        }

        dto.scopes.forEach { scope ->
            val ruleTypesSet = mutableSetOf<FifaRuleTypes>()
            scope.rules.forEach { rule ->
                val ruleType = FifaRuleTypes.valueOf(rule.type.toString())

                if (ruleType in ruleTypesSet) {
                    throw IllegalArgumentException("Duplicate rule type found in scope")
                } else {
                    ruleTypesSet.add(ruleType)
                }

                val minValue = ruleType.minValue
                val maxValue = ruleType.maxValue

                if (rule.value < minValue || rule.value > maxValue) {
                    throw IllegalArgumentException("Rule value out of range")
                }
            }
        }

    }
}