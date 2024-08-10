package net.stakemetrics.application.repositories

import net.stakemetrics.application.entities.FifaStrategyRule

interface IFifaStrategyRuleRepository {
    fun saveAll(rules: List<FifaStrategyRule>)
}