package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.fifa.FifaPlayer
import com.stakemetrics.backend.domain.ports.fifa.FifaPlayerRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaPlayerModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaPlayerJpaRepository
import java.util.UUID
import org.springframework.stereotype.Repository

@Repository
class FifaPlayerRepository(private val fifaPlayerJpaRepository: FifaPlayerJpaRepository) : FifaPlayerRepositoryPort {
    override fun findByName(name: String): FifaPlayer? {
        return fifaPlayerJpaRepository.findByName(name)?.toDomain()
    }

    override fun findById(id: UUID): FifaPlayer? {
        return fifaPlayerJpaRepository.findById(id).let { if (it.isPresent) it.get().toDomain() else null }
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