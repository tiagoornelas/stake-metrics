package net.stakemetrics.application.service

import java.util.UUID
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.repositories.IFifaPlayerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FifaPlayerService @Autowired constructor(private val fifaPlayerRepository: IFifaPlayerRepository) {

    fun save(player: FifaPlayer) {
        fifaPlayerRepository.save(player)
    }

    fun findById(id: UUID): FifaPlayer {
        return fifaPlayerRepository.findById(id)
    }

    fun findByName(name: String): FifaPlayer {
        return fifaPlayerRepository.findByName(name)
    }

    fun findAll(): List<FifaPlayer> {
        return fifaPlayerRepository.findAll()
    }

}