package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.persistence.models.FifaStrategyScopeModel
import org.springframework.data.jpa.repository.JpaRepository

interface FifaStrategyScopeJpaRepository : JpaRepository<FifaStrategyScopeModel, UUID> {
}