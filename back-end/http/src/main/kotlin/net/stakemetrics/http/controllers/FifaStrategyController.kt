package net.stakemetrics.http.controllers

import java.util.UUID
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.service.FifaStrategyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fifa/strategy")
class FifaStrategyController(
    private val fifaStrategyService: FifaStrategyService
) {

    @PostMapping
    fun saveStrategy(@RequestBody request: FifaStrategyDTO.FifaStrategyRequest): ResponseEntity<FifaStrategyDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaStrategyService.save(userEmail, request)
        return ResponseEntity.status(HttpStatus.OK).body(FifaStrategyDTO.FifaStrategyWriteResponse())
    }

    @PutMapping("/status/{strategyId}")
    fun updateStrategyStatus(
        @RequestBody request: FifaStrategyDTO.FifaStrategyStatusRequest,
        @PathVariable strategyId: UUID
    ): ResponseEntity<FifaStrategyDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaStrategyService.updateStrategyStatus(userEmail, strategyId, request.status)
        return ResponseEntity.status(HttpStatus.OK).body(FifaStrategyDTO.FifaStrategyWriteResponse())
    }

    @PostMapping("/restart/{strategyId}")
    fun restartStrategy(@PathVariable strategyId: UUID): ResponseEntity<FifaStrategyDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaStrategyService.restartStrategy(userEmail, strategyId)
        return ResponseEntity.status(HttpStatus.OK).body(FifaStrategyDTO.FifaStrategyWriteResponse())
    }

    @DeleteMapping("/{strategyId}")
    fun deleteStrategy(@PathVariable strategyId: UUID): ResponseEntity<FifaStrategyDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaStrategyService.deleteStrategy(userEmail, strategyId)
        return ResponseEntity.status(HttpStatus.OK).body(FifaStrategyDTO.FifaStrategyWriteResponse())
    }

    @GetMapping("/params")
    fun getStrategyParams(): ResponseEntity<FifaStrategyDTO.FifaStrategyParamsResponse> {
        val (leagues, marketTypes, players, ruleTypes, matchupTypes, scopeTypes) = fifaStrategyService.getStrategyParams()
        val response =
            FifaStrategyDTO.FifaStrategyParamsResponse(
                leagues,
                marketTypes,
                players,
                ruleTypes,
                matchupTypes,
                scopeTypes
            )
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/{strategyId}")
    fun getStrategy(@PathVariable strategyId: UUID): ResponseEntity<FifaStrategyDTO.FifaStrategyReadResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val strategy = fifaStrategyService.getStrategy(userEmail, strategyId)
        return ResponseEntity.status(HttpStatus.OK).body(strategy)
    }

    @GetMapping("/profits/{strategyId}")
    fun listCumulativeProfits(@PathVariable strategyId: UUID): ResponseEntity<FifaStrategyDTO.CumulativeProfitResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val cumulativeProfit = fifaStrategyService.listCumulativeProfits(userEmail, strategyId)
        return ResponseEntity.status(HttpStatus.OK).body(
            FifaStrategyDTO.CumulativeProfitResponse(
                strategyId,
                cumulativeProfit
            )
        )
    }

    @GetMapping("/statistics")
    fun listAllStrategies(): ResponseEntity<FifaStrategyDTO.FifaStrategyListResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val strategies = fifaStrategyService.listAllStrategiesStatistics(userEmail)
        return ResponseEntity.status(HttpStatus.OK).body(FifaStrategyDTO.FifaStrategyListResponse(strategies))
    }

}