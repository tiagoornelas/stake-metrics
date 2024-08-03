package com.stakemetrics.backend.domain.ports.fifa

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyRule

interface FifaStrategyRuleRepositoryPort {
    fun saveAll(rules: List<FifaStrategyRule>)
}