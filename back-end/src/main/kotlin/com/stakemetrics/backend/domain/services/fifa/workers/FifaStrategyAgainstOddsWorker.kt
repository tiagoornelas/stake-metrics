package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import org.springframework.stereotype.Service

@Service
class FifaStrategyAgainstOddsWorker {
    fun runStrategyAgainstOdds(request: FifaDTO.FifaStrategyAgainstOddRequest) {
        println(request)
    }
}