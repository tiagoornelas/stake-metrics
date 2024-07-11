package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.domain.enums.FifaLeagueStatusTypes
import com.stakemetrics.backend.plugins.persistence.models.FifaLeagueModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface FifaLeagueJpaRepository: JpaRepository<FifaLeagueModel, UUID> {
    fun findAllByStatus(status: FifaLeagueStatusTypes): List<FifaLeagueModel>
    fun existsByIntegrationId(integrationId: Int): Boolean
    fun findByIntegrationId(integrationId: Int): FifaLeagueModel?
}