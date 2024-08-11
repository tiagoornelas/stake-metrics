package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.service.FifaMatchService
import net.stakemetrics.application.service.FifaStrategyService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/queue")
class QueueController(
    private val fifaMatchService: FifaMatchService,
    private val fifaStrategyService: FifaStrategyService
) {

    @PostMapping("/fifa/save-match-result")
    fun saveMatchResult(@RequestBody payload: FifaDTO.FifaMatchRequest) {
        fifaMatchService.saveMatch(payload)
    }

    @PostMapping("/fifa/run-strategy-against-odds")
    fun runStrategyAgainstOdds(@RequestBody payload: FifaDTO.FifaStrategyAgainstOddRequest) {
        fifaStrategyService.runStrategyAgainstOdds(payload)
    }
}