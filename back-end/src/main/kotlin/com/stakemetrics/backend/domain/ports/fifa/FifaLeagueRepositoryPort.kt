package com.stakemetrics.backend.domain.ports.fifa

import com.stakemetrics.backend.domain.entities.fifa.FifaLeague
import java.util.UUID

interface FifaLeagueRepositoryPort {
    fun save(fifaLeague: FifaLeague)
    fun existsByIntegrationId(integrationId: Int): Boolean
    fun listActiveLeagues(): List<FifaLeague>
    fun findByIntegrationId(integrationId: Int): FifaLeague?
    fun findById(id: UUID): FifaLeague?
}