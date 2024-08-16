package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
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
    fun saveMatchResult(@RequestBody payload: FifaDataSourceDTO.FifaMatchRequest) {
        fifaMatchService.buildAndSave(payload)
    }

    @PostMapping("/fifa/run-strategy-against-odds")
    fun runStrategyAgainstOdds(@RequestBody payload: FifaStrategyDTO.FifaStrategyAgainstOddRequest) {
        fifaStrategyService.runStrategyAgainstOdds(payload)
    }

//    @PostMapping("/fifa/save-bet")
//    fun saveBet(@RequestBody payload: FifaDataSourceDTO.FifaBetRequest) {
//        fifaMatchService.saveBet(payload)
//    }
//
//    @PostMapping("/messenger/send-message")
//    fun sendMessage(@RequestBody payload: MessengerDTO.SendMessageRequest) {
//        println("Sending message: $payload")
//    }
//
//    @PostMapping("/messenger/edit-message")
//    fun editMessage(@RequestBody payload: MessengerDTO.EditMessageRequest) {
//        println("Editing message: $payload")
//    }
}