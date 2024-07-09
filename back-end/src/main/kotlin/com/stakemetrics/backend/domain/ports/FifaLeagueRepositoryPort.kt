package com.stakemetrics.backend.domain.ports

import com.stakemetrics.backend.domain.entities.FifaLeague

interface FifaLeagueRepositoryPort {
    fun save(fifaLeague: FifaLeague)
    fun existsByIntegrationId(integrationId: Int): Boolean
    fun listActiveLeagues(): List<FifaLeague>
}