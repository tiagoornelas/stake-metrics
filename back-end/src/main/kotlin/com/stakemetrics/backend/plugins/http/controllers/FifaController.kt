package com.stakemetrics.backend.plugins.http.controllers

import com.stakemetrics.backend.domain.ports.FifaLeagueRepositoryPort
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import com.stakemetrics.backend.plugins.http.dto.toResponse
import com.stakemetrics.backend.plugins.http.ports.FifaServicePort
import com.stakemetrics.backend.service.CloudTaskClientService
import jakarta.servlet.http.HttpServletRequest
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
class FifaController(
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

    @PostMapping("/match")
    fun saveMatchResults(@RequestBody match: FifaDTO.FifaMatchRequest): ResponseEntity<FifaDTO.FifaMatchResponse> {
        fifaServicePort.saveMatch(
            match.integrationId,
            match.time,
            match.status,
            match.leagueId,
            match.home,
            match.away,
            match.homeGoalsAtHalfTime,
            match.awayGoalsAtHalfTime,
            match.homeGoalsAtFullTime,
            match.awayGoalsAtFullTime,
            match.totalGoalsAtHalfTime,
            match.totalGoalsAtFullTime,
            match.winner
        )
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaMatchResponse())
    }

    @PostMapping("/next-match/odds")
    fun analyzeNextMatchOdds(@RequestBody odds: Any): ResponseEntity<Any> {
        println(odds)
        return ResponseEntity.status(HttpStatus.OK).body("Odds analyzed")
    }

}