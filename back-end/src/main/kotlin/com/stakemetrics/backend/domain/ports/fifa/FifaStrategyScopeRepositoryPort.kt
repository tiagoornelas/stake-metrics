package com.stakemetrics.backend.domain.ports.fifa

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyScope

interface FifaStrategyScopeRepositoryPort {
    fun save(rule: FifaStrategyScope)
    fun saveAll(rules: List<FifaStrategyScope>)
}