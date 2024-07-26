package com.stakemetrics.backend.domain.ports.fifa

import com.stakemetrics.backend.domain.entities.fifa.FifaRule

interface FifaRuleRepositoryPort {
    fun save(rule: FifaRule)
    fun saveAll(rules: List<FifaRule>)
}