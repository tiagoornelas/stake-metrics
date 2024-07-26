package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.domain.enums.EnvironmentTypes
import com.stakemetrics.backend.domain.ports.fifa.FifaStrategyRepositoryPort
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import com.stakemetrics.backend.service.CloudTaskClientService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class FifaStrategyEnqueuer(
    private val fifaStrategyRepositoryPort: FifaStrategyRepositoryPort,
    private val cloudTaskClientService: CloudTaskClientService,
    private val fifaStrategyAgainstOddsWorker: FifaStrategyAgainstOddsWorker,
    private val environment: Environment
) {
    fun enqueue(odds: FifaDTO.FifaOddRequest, request: HttpServletRequest) {
        val strategies = fifaStrategyRepositoryPort.getAllStrategies()
        val currentEnvironment = environment.getProperty("current.environment")

        strategies.forEach { strategy ->
            val payload = FifaDTO.FifaStrategyAgainstOddRequest(strategy, odds)
            if (currentEnvironment == EnvironmentTypes.DEV.name) {
                callMethodDirectly(payload)
            } else {
                enqueueCloudTask(payload, request)
            }
        }
    }

    private fun enqueueCloudTask(payload: FifaDTO.FifaStrategyAgainstOddRequest, request: HttpServletRequest) {
        cloudTaskClientService.enqueueCheckOddForStrategyTask(payload, request)
    }

    private fun callMethodDirectly(payload: FifaDTO.FifaStrategyAgainstOddRequest) {
        fifaStrategyAgainstOddsWorker.runStrategyAgainstOdds(payload)
    }
}