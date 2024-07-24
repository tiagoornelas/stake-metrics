package com.stakemetrics.backend.plugins.http.ports

import com.stakemetrics.backend.domain.entities.fifa.FifaLeague
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import org.springframework.stereotype.Service

@Service
interface FifaServicePort {
    fun listActiveLeagues(): List<FifaLeague>
    fun getLastResultTime(): Long
    fun saveMatch(dto: FifaDTO.FifaMatchRequest)
    fun saveStrategy(userEmail: String, dto: FifaDTO.FifaStrategyRequest)
}