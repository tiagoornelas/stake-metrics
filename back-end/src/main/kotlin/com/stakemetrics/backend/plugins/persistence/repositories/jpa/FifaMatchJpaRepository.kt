package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.plugins.persistence.models.FifaMatchModel
import java.util.Optional
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface FifaMatchJpaRepository : JpaRepository<FifaMatchModel, UUID> {
    fun findTopByOrderByTimeDesc(): Optional<FifaMatchModel>
    fun findByIntegrationId(integrationId: Int): Optional<FifaMatchModel>
}