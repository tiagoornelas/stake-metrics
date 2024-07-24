package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.plugins.persistence.models.FifaStrategyModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface FifaStrategyJpaRepository : JpaRepository<FifaStrategyModel, UUID> {
}