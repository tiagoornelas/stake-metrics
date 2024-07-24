package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.plugins.persistence.models.FifaRuleModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface FifaRuleJpaRepository : JpaRepository<FifaRuleModel, UUID> {
}