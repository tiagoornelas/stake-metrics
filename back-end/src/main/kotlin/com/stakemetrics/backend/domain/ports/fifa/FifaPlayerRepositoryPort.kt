package com.stakemetrics.backend.domain.ports.fifa

import com.stakemetrics.backend.domain.entities.fifa.FifaPlayer
import java.util.UUID

interface FifaPlayerRepositoryPort {
    fun save(fifaPlayer: FifaPlayer)
    fun findByName(name: String): FifaPlayer?
    fun findById(id: UUID): FifaPlayer?
    fun findAll(): List<FifaPlayer>
}