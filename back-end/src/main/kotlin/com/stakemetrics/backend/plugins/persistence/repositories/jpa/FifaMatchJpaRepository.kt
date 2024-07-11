package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.plugins.persistence.models.FifaMatchModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface FifaMatchJpaRepository: JpaRepository<FifaMatchModel, UUID> {
    fun findTopByOrderByTimeDesc(): FifaMatchModel
    fun findByIntegrationId(integrationId: Int): FifaMatchModel
}