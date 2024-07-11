package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.FifaLeague
import com.stakemetrics.backend.domain.enums.FifaLeagueStatusTypes
import com.stakemetrics.backend.domain.ports.FifaLeagueRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaLeagueModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaLeagueJpaRepository
import org.springframework.stereotype.Repository

@Repository
class FifaLeagueRepository(private val fifaLeagueJpaRepository: FifaLeagueJpaRepository) : FifaLeagueRepositoryPort {
    override fun save(fifaLeague: FifaLeague) {
        fifaLeagueJpaRepository.save(fifaLeague.toModel())
    }

    override fun existsByIntegrationId(integrationId: Int): Boolean {
        return fifaLeagueJpaRepository.existsByIntegrationId(integrationId)
    }

    override fun listActiveLeagues(): List<FifaLeague> {
        return fifaLeagueJpaRepository.findAllByStatus(FifaLeagueStatusTypes.ACTIVE).map { it.toDomain() }
    }

    override fun findByIntegrationId(integrationId: Int): FifaLeague? {
        return fifaLeagueJpaRepository.findByIntegrationId(integrationId)?.toDomain()
    }

}

fun FifaLeague.toModel(): FifaLeagueModel {
    return FifaLeagueModel(id, integrationId, status, name, link)
}