package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaLeague

interface IFifaLeagueRepository {
    fun save(fifaLeague: FifaLeague)
    fun existsByIntegrationId(integrationId: Long): Boolean
    fun listActiveLeagues(): List<FifaLeague>
    fun findByIntegrationId(integrationId: Long): FifaLeague?
    fun findById(id: UUID): FifaLeague?
}