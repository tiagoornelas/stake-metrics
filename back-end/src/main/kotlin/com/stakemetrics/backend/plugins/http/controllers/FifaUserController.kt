package com.stakemetrics.backend.plugins.http.controllers

import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import com.stakemetrics.backend.plugins.http.ports.FifaServicePort
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fifa")
class FifaUserController(
    private val fifaServicePort: FifaServicePort,
) {

    @PostMapping("/strategy")
    fun saveStrategy(@RequestBody request: FifaDTO.FifaStrategyRequest): ResponseEntity<FifaDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaServicePort.saveStrategy(userEmail, request)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyWriteResponse())
    }

    @PutMapping("/strategy/status/{strategyId}")
    fun updateStrategyStatus(
        @RequestBody request: FifaDTO.FifaStrategyStatusRequest,
        @PathVariable strategyId: UUID
    ): ResponseEntity<FifaDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaServicePort.updateStrategyStatus(userEmail, strategyId, request.status)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyWriteResponse())
    }

    @DeleteMapping("/strategy/{strategyId}")
    fun deleteStrategy(@PathVariable strategyId: UUID): ResponseEntity<FifaDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaServicePort.deleteStrategy(userEmail, strategyId)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyWriteResponse())
    }

    @GetMapping("/strategy/params")
    fun getStrategyParams(): ResponseEntity<FifaDTO.FifaStrategyParamsResponse> {
        val (leagues, marketTypes, players, ruleTypes, matchupTypes, scopeTypes) = fifaServicePort.getStrategyParams()
        val response = FifaDTO.FifaStrategyParamsResponse(leagues, marketTypes, players, ruleTypes, matchupTypes, scopeTypes)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/strategy/{strategyId}")
    fun getStrategy(@PathVariable strategyId: UUID): ResponseEntity<FifaDTO.FifaStrategyReadResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val strategy = fifaServicePort.getStrategy(userEmail, strategyId)
        return ResponseEntity.status(HttpStatus.OK).body(strategy)
    }

    @GetMapping("/strategy")
    fun listAllStrategies(): ResponseEntity<FifaDTO.FifaStrategyListResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val strategies = fifaServicePort.listAllStrategies(userEmail)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyListResponse(strategies))
    }

}