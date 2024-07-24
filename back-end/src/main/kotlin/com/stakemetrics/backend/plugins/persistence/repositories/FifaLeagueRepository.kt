package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.fifa.FifaLeague
import com.stakemetrics.backend.domain.enums.fifa.FifaLeagueStatusTypes
import com.stakemetrics.backend.domain.ports.fifa.FifaLeagueRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaLeagueModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaLeagueJpaRepository
import java.util.UUID
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

    override fun findById(id: UUID): FifaLeague? {
        return fifaLeagueJpaRepository.findById(id).let { if (it.isPresent) it.get().toDomain() else null }
    }

}

fun FifaLeague.toModel(): FifaLeagueModel {
    return FifaLeagueModel(id, integrationId, status, name, link)
}