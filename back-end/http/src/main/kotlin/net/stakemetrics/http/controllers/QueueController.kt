package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.dtos.MessengerDTO
import net.stakemetrics.application.entities.dtos.QueueDTO
import net.stakemetrics.application.service.FifaBetService
import net.stakemetrics.application.service.FifaMatchService
import net.stakemetrics.application.service.FifaOddSnapshotService
import net.stakemetrics.application.service.FifaStrategyService
import net.stakemetrics.application.service.IMessengerService
import net.stakemetrics.application.workers.BetResultsReporter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/queue")
class QueueController(
    private val fifaBetService: FifaBetService,
    private val fifaMatchService: FifaMatchService,
    private val messengerService: IMessengerService,
    private val betResultsReporter: BetResultsReporter,
    private val fifaStrategyService: FifaStrategyService,
    private val fifaOddSnapshotService: FifaOddSnapshotService,
) {

    @PostMapping("/fifa/save-match-result")
    fun saveMatchResult(@RequestBody payload: FifaDataSourceDTO.FifaMatchRequest): ResponseEntity<QueueDTO.Response> {
        fifaMatchService.buildAndSave(payload)
        return ResponseEntity.ok(QueueDTO.Response())
    }

    @PostMapping("/fifa/run-trend-analysis")
    fun runTrendAnalysis(@RequestBody payload: FifaDataSourceDTO.FifaOddRequest): ResponseEntity<QueueDTO.Response> {
        fifaOddSnapshotService.runTrendAnalysis(payload)
        return ResponseEntity.ok(QueueDTO.Response())
    }

    @PostMapping("/fifa/run-strategy-against-odds")
    fun runStrategyAgainstOdds(@RequestBody payload: FifaStrategyDTO.FifaStrategyAgainstOddRequest): ResponseEntity<QueueDTO.Response> {
        fifaStrategyService.runStrategyAgainstOdds(payload)
        return ResponseEntity.ok(QueueDTO.Response())
    }

    @PostMapping("/fifa/bet-queue")
    fun enqueueBetTask(@RequestBody payload: FifaBetDTO.BetRequest): ResponseEntity<QueueDTO.Response> {
        fifaBetService.bet(payload)
        return ResponseEntity.ok(QueueDTO.Response())
    }

    @PostMapping("/fifa/close-bet")
    fun closeBet(@RequestBody payload: FifaBetDTO.CloseBetRequest): ResponseEntity<QueueDTO.Response> {
        fifaBetService.closeBet(payload.bet)
        return ResponseEntity.ok(QueueDTO.Response())
    }

    @PostMapping("/fifa/discard-hanging-bet")
    fun discardHangingBet(@RequestBody payload: FifaBetDTO.CloseBetRequest): ResponseEntity<QueueDTO.Response> {
        fifaBetService.discardHangingBet(payload.bet)
        return ResponseEntity.ok(QueueDTO.Response())
    }

    @PostMapping("/fifa/close-odd-snapshot")
    fun closeOddSnapshot(@RequestBody payload: FifaOddSnapshot): ResponseEntity<QueueDTO.Response> {
        fifaOddSnapshotService.closeOddSnapshot(payload)
        return ResponseEntity.ok(QueueDTO.Response())
    }

    @PostMapping("/fifa/message-queue")
    fun enqueueMessageTask(@RequestBody payload: MessengerDTO.EnqueueRequest): ResponseEntity<QueueDTO.Response> {
        messengerService.sendToChat(payload.messengerChat, payload.message, payload.messageId)
        return ResponseEntity.ok(QueueDTO.Response())
    }

    @PutMapping("/fifa/message-queue")
    fun enqueueEditMessageTask(@RequestBody payload: MessengerDTO.EditMessageEnqueueRequest): ResponseEntity<QueueDTO.Response> {
        messengerService.editMessage(payload)
        return ResponseEntity.ok(QueueDTO.Response())
    }

    @PostMapping("/fifa/bet-report-queue")
    fun reportBetResults(@RequestBody payload: MessengerDTO.ReportBetResultsRequest): ResponseEntity<QueueDTO.Response> {
        betResultsReporter.reportChat(payload.messengerChat)
        return ResponseEntity.ok(QueueDTO.Response())
    }

}