package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.application.entities.enums.FifaLeagueStatusTypes
import net.stakemetrics.persistence.models.FifaLeagueModel
import org.springframework.data.jpa.repository.JpaRepository

interface FifaLeagueJpaRepository : JpaRepository<FifaLeagueModel, UUID> {
    fun findAllByStatus(status: FifaLeagueStatusTypes): List<FifaLeagueModel>
    fun existsByIntegrationId(integrationId: Long): Boolean
    fun findByIntegrationId(integrationId: Long): FifaLeagueModel?
}