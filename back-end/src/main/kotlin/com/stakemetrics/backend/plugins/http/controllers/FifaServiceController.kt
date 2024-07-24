package com.stakemetrics.backend.plugins.http.controllers

import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import com.stakemetrics.backend.plugins.http.dto.toResponse
import com.stakemetrics.backend.plugins.http.ports.FifaServicePort
import com.stakemetrics.backend.service.CloudTaskClientService
import jakarta.servlet.http.HttpServletRequest
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/service/fifa")
class FifaServiceController(
    private val fifaServicePort: FifaServicePort,
    private val cloudTaskClientService: CloudTaskClientService
) {

    @GetMapping("/last-result-time")
    fun getLastResultTime(): ResponseEntity<FifaDTO.LastResultTimeResponse> {
        val lastResultTime = fifaServicePort.getLastResultTime()
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.LastResultTimeResponse(lastResultTime))
    }

    @GetMapping("/leagues")
    fun getActiveFifaLeagues(): ResponseEntity<FifaDTO.FifaLeaguesResponse> {
        val activeLeagues = fifaServicePort.listActiveLeagues()
        return ResponseEntity.status(HttpStatus.OK)
            .body(FifaDTO.FifaLeaguesResponse(activeLeagues.map { it.toResponse() }))
    }

    @PostMapping("/match/enqueue")
    fun enqueueSaveMatchResults(
        @RequestBody match: FifaDTO.FifaMatchRequest,
        request: HttpServletRequest
    ): ResponseEntity<FifaDTO.FifaMatchResponse> {
        cloudTaskClientService.enqueueSaveMatchResultTask(match, request)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaMatchResponse())
    }

    @PostMapping("/upcoming-match/odds/enqueue")
    fun enqueueAnalyzeNextMatchOdds(
        @RequestBody odds: FifaDTO.FifaOddRequest,
        request: HttpServletRequest
    ): ResponseEntity<FifaDTO.FifaOddResponse> {
        cloudTaskClientService.enqueueReceiveUpcomingMatchOddsTask(odds, request)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaOddResponse())
    }

    @PostMapping("/upcoming-match/odds")
    fun receiveUpcomingMatchOdds(@RequestBody odds: FifaDTO.FifaOddRequest): ResponseEntity<FifaDTO.FifaOddResponse> {
        println(odds)
        // Should:
        // Fetch all strategies for the given league
        // Run the strategies against the odds, enqueue the analysis for production environment
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaOddResponse())
    }

    @PostMapping("/strategy-against-odds/{strategyId}")
    fun runStrategyAgainstOdds(@RequestBody odds: FifaDTO.FifaOddRequest, @PathVariable strategyId: UUID): ResponseEntity<FifaDTO.FifaOddResponse> {
        println(odds)
        // Should:
        // Fetch all strategies for the given league
        // Run the strategies against the odds, enqueue the analysis for production environment
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaOddResponse())
    }

}