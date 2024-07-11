package com.stakemetrics.backend.domain.ports

import com.stakemetrics.backend.domain.entities.FifaPlayer

interface FifaPlayerRepositoryPort {
    fun findByName(name: String): FifaPlayer?
    fun save(fifaPlayer: FifaPlayer)
}