package com.stakemetrics.backend.domain.ports.fifa

import com.stakemetrics.backend.domain.entities.fifa.FifaMatch

interface FifaMatchRepositoryPort {
    fun save(fifaMatch: FifaMatch)
    fun findLatestMatch(): FifaMatch?
    fun findByIntegrationId(integrationId: Int): FifaMatch?
}