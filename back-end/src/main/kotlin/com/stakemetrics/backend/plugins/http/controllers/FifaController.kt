package com.stakemetrics.backend.plugins.http.controllers

import com.stakemetrics.backend.domain.ports.FifaLeagueRepositoryPort
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import com.stakemetrics.backend.plugins.http.dto.toResponse
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/fifa")
class FifaController(private val fifaLeaguesRepositoryPort: FifaLeagueRepositoryPort) {

    @GetMapping("/last-result-time")
    fun getLastResultTime(): ResponseEntity<FifaDTO.LastResultTimeResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.LastResultTimeResponse(1720407600))
    }

    @GetMapping("/leagues")
    fun getActiveFifaLeagues(): ResponseEntity<FifaDTO.FifaLeaguesResponse> {
        val activeLeagues = fifaLeaguesRepositoryPort.listActiveLeagues()
        return ResponseEntity.status(HttpStatus.OK)
            .body(FifaDTO.FifaLeaguesResponse(activeLeagues.map { it.toResponse() }))
    }

    @PostMapping("/match")
    fun saveMatchResults(@RequestBody match: FifaDTO.FifaMatchRequest): ResponseEntity<FifaDTO.FifaMatchResponse> {
        println(match)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaMatchResponse(UUID.randomUUID()))
    }

    @PostMapping("/next-match/odds")
    fun analyzeNextMatchOdds() {
        // Analyze next match odds
    }

}