package net.stakemetrics.application.service

import net.stakemetrics.application.entities.exceptions.NotFoundException
import java.util.UUID
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.repositories.IFifaLeagueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FifaLeagueService @Autowired constructor(private val fifaLeagueRepository: IFifaLeagueRepository) {

    fun save(fifaLeague: FifaLeague) {
        fifaLeagueRepository.save(fifaLeague)
    }

    fun findById(id: UUID): FifaLeague {
        return fifaLeagueRepository.findById(id) ?: throw NotFoundException(
            "League",
            "id",
            id.toString()
        )
    }

    fun listActiveLeagues(): List<FifaLeague> {
        return fifaLeagueRepository.listActiveLeagues()
    }

    fun findByIntegrationId(integrationId: Long): FifaLeague {
        return fifaLeagueRepository.findByIntegrationId(integrationId) ?: throw NotFoundException(
            "League",
            "integrationId",
            integrationId.toString()
        )
    }

    fun existsByIntegrationId(integrationId: Long): Boolean {
        return fifaLeagueRepository.findByIntegrationId(integrationId) != null
    }

}