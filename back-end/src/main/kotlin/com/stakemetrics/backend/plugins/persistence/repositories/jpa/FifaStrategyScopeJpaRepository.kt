package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.plugins.persistence.models.FifaStrategyScopeModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface FifaStrategyScopeJpaRepository : JpaRepository<FifaStrategyScopeModel, UUID> {
}