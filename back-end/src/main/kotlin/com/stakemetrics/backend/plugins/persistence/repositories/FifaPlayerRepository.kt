package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.FifaPlayer
import com.stakemetrics.backend.domain.ports.FifaPlayerRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaPlayerModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaPlayerJpaRepository
import org.springframework.stereotype.Repository

@Repository
class FifaPlayerRepository(private val fifaPlayerJpaRepository: FifaPlayerJpaRepository) : FifaPlayerRepositoryPort {
    override fun findByName(name: String): FifaPlayer? {
        return fifaPlayerJpaRepository.findByName(name)?.toDomain()
    }

    override fun save(fifaPlayer: FifaPlayer) {
        fifaPlayerJpaRepository.save(fifaPlayer.toModel())
    }
}

fun FifaPlayer.toModel(): FifaPlayerModel {
    return FifaPlayerModel(
        id,
        name,
        league?.toModel()
    )
}