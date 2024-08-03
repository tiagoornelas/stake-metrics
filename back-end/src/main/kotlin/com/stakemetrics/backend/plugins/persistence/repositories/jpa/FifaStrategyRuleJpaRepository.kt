package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.plugins.persistence.models.FifaStrategyRuleModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface FifaStrategyRuleJpaRepository : JpaRepository<FifaStrategyRuleModel, UUID> {
}