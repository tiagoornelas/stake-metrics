package com.stakemetrics.backend.domain.ports

import com.stakemetrics.backend.domain.entities.FifaMatch

interface FifaMatchRepositoryPort {
    fun save(fifaMatch: FifaMatch)
    fun findLatestMatch(): FifaMatch?
    fun findByIntegrationId(integrationId: Int): FifaMatch?
}