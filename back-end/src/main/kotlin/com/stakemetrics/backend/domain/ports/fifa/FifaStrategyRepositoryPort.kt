package com.stakemetrics.backend.domain.ports.fifa

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy

interface FifaStrategyRepositoryPort {
    fun save(strategy: FifaStrategy)
}