package com.stakemetrics.backend.plugins.http.ports

import com.stakemetrics.backend.domain.entities.fifa.FifaLeague
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyStatus
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import jakarta.servlet.http.HttpServletRequest
import java.util.UUID
import org.springframework.stereotype.Service

@Service
interface FifaServicePort {
    fun listActiveLeagues(): List<FifaLeague>
    fun getLastResultTime(): Long
    fun saveMatch(dto: FifaDTO.FifaMatchRequest)
    fun saveStrategy(userEmail: String, dto: FifaDTO.FifaStrategyRequest)
    fun enqueueStrategiesAgainstOdds(odds: FifaDTO.FifaOddRequest, request: HttpServletRequest)
    fun updateStrategyStatus(userEmail: String, strategyId: UUID, status: FifaStrategyStatus)
    fun deleteStrategy(userEmail: String, strategyId: UUID)
    fun getStrategyParams(): FifaDTO.FifaStrategyParamsResponse
    fun getStrategy(userEmail: String, strategyId: UUID): FifaDTO.FifaStrategyReadResponse
    fun listAllStrategies(userEmail: String): List<FifaDTO.FifaStrategySingleResponse>
}