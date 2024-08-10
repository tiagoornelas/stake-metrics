package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaPlayer

interface IFifaPlayerRepository {
    fun save(fifaPlayer: FifaPlayer)
    fun findByName(name: String): FifaPlayer
    fun findById(id: UUID): FifaPlayer
    fun findAll(): List<FifaPlayer>
}