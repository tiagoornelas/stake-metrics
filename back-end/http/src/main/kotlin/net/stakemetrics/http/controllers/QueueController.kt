package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.workers.enqueuers.FifaStrategyEnqueuer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/queue")
class QueueController(private val fifaStrategyEnqueuer: FifaStrategyEnqueuer) {

    @PostMapping
    fun receiveMatchAgainstOdds(@RequestBody payload: FifaDTO.FifaOddRequest) {
        return fifaStrategyEnqueuer.enqueue(payload)
    }

    @PostMapping("/fifa/strategy-against-odds")
    fun runStrategyAgainstOdds(@RequestBody payload: FifaDTO.FifaStrategyAgainstOddRequest) {
        println("Tarefa chegou na fila!, Payload: $payload")
    }
}