package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.service.FifaStrategyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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

    @PostMapping("/exists")
    fun checkStrategyExistence(@RequestBody request: FifaStrategyDTO.FifaStrategyRequest): ResponseEntity<FifaStrategyDTO.FifaStrategyExistsResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val exists = fifaStrategyService.checkStrategyExistenceForUser(userEmail, request)
        return ResponseEntity.status(HttpStatus.OK).body(exists)
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

    @PutMapping("/status/inactivate/all")
    fun updateStrategyStatus(): ResponseEntity<FifaStrategyDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaStrategyService.inactivateAllByUserEmail(userEmail)
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

    @GetMapping("/report/simple/{strategyId}")
    fun getSimpleReport(
        @PathVariable strategyId: UUID,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<FifaStrategyDTO.SimpleReportResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val report = fifaStrategyService.getSimpleReport(userEmail, strategyId, days)
        return ResponseEntity.status(HttpStatus.OK).body(report)
    }

    @GetMapping("/report/detailed/{strategyId}")
    fun getDetailedReport(
        @PathVariable strategyId: UUID,
        @RequestParam(defaultValue = "30") days: Int
    ): ResponseEntity<FifaStrategyDTO.DetailedReportResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val report = fifaStrategyService.getDetailedReport(userEmail, strategyId, days)
        return ResponseEntity.status(HttpStatus.OK).body(report)
    }

}