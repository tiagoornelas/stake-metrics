package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaLeague

interface IFifaLeagueRepository {
    fun save(fifaLeague: FifaLeague)
    fun existsByIntegrationId(integrationId: Int): Boolean
    fun listActiveLeagues(): List<FifaLeague>
    fun findByIntegrationId(integrationId: Int): FifaLeague?
    fun findById(id: UUID): FifaLeague?
}