package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IFifaPlayerRepository
import net.stakemetrics.persistence.jpa.FifaPlayerJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class FifaPlayerRepository(private val fifaPlayerJpaRepository: FifaPlayerJpaRepository) : IFifaPlayerRepository {

    override fun findByName(name: String): FifaPlayer {
        val queriedFifaPlayer = fifaPlayerJpaRepository.findByName(name) ?: throw NotFoundException(
            "FifaPlayer",
            "name",
            name
        )
        return queriedFifaPlayer.toDomain()
    }

    override fun findById(id: UUID): FifaPlayer {
        return fifaPlayerJpaRepository.findById(id).let {
            if (it.isPresent) it.get().toDomain() else throw NotFoundException(
                "FifaPlayer",
                "id",
                id.toString()
            )
        }
    }

    override fun findAll(): List<FifaPlayer> {
        return fifaPlayerJpaRepository.findAll().map { it.toDomain() }
    }

    override fun save(fifaPlayer: FifaPlayer) {
        fifaPlayerJpaRepository.save(fifaPlayer.toModel())
    }
}
