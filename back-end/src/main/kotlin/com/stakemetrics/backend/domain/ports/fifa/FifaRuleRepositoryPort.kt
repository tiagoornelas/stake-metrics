package com.stakemetrics.backend.domain.ports.fifa

import com.stakemetrics.backend.domain.entities.fifa.FifaRule
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy

interface FifaRuleRepositoryPort {
    fun save(rule: FifaRule, strategy: FifaStrategy)
    fun saveAll(rules: List<FifaRule>, strategy: FifaStrategy)
}