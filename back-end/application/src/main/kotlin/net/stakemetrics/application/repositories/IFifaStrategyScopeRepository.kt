package net.stakemetrics.application.repositories

import net.stakemetrics.application.entities.FifaStrategyScope

interface IFifaStrategyScopeRepository {
    fun save(rule: FifaStrategyScope)
    fun saveAll(rules: List<FifaStrategyScope>)
}