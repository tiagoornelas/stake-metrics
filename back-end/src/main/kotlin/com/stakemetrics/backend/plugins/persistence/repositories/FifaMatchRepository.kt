package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.FifaMatch
import com.stakemetrics.backend.domain.ports.FifaMatchRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaMatchModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaMatchJpaRepository
import org.springframework.stereotype.Repository

@Repository
class FifaMatchRepository(private val fifaMatchJpaRepository: FifaMatchJpaRepository) : FifaMatchRepositoryPort {
    override fun save(fifaMatch: FifaMatch) {
        fifaMatchJpaRepository.save(fifaMatch.toModel())
    }

    override fun findLatestMatch(): FifaMatch? {
        return fifaMatchJpaRepository.findTopByOrderByTimeDesc().toDomain()
    }

    override fun findByIntegrationId(integrationId: Int): FifaMatch? {
        return fifaMatchJpaRepository.findByIntegrationId(integrationId).toDomain()
    }
}

fun FifaMatch.toModel(): FifaMatchModel {
    return FifaMatchModel(
        id,
        integrationId,
        time,
        status,
        league?.toModel(),
        home?.toModel(),
        away?.toModel(),
        homeGoalsAtHalfTime,
        homeGoalsAtFullTime,
        awayGoalsAtHalfTime,
        awayGoalsAtFullTime,
        totalGoalsAtHalfTime,
        totalGoalsAtFullTime,
        winner?.toModel()
    )
}