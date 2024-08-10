package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.enums.FifaLeagueStatusTypes
import net.stakemetrics.application.repositories.IFifaLeagueRepository
import net.stakemetrics.persistence.jpa.FifaLeagueJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class FifaLeagueRepository(private val fifaLeagueJpaRepository: FifaLeagueJpaRepository) : IFifaLeagueRepository {

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