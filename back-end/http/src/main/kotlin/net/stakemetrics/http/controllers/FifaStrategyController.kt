package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.FifaDTO
import java.util.UUID
import net.stakemetrics.application.service.FifaStrategyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fifa")
class FifaStrategyController(
    private val fifaStrategyService: FifaStrategyService
) {

    @PostMapping("/strategy")
    fun saveStrategy(@RequestBody request: FifaDTO.FifaStrategyRequest): ResponseEntity<FifaDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaStrategyService.save(userEmail, request)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyWriteResponse())
    }

    @PutMapping("/strategy/status/{strategyId}")
    fun updateStrategyStatus(
        @RequestBody request: FifaDTO.FifaStrategyStatusRequest,
        @PathVariable strategyId: UUID
    ): ResponseEntity<FifaDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaStrategyService.updateStrategyStatus(userEmail, strategyId, request.status)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyWriteResponse())
    }

    @DeleteMapping("/strategy/{strategyId}")
    fun deleteStrategy(@PathVariable strategyId: UUID): ResponseEntity<FifaDTO.FifaStrategyWriteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaStrategyService.deleteStrategy(userEmail, strategyId)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyWriteResponse())
    }

    @GetMapping("/strategy/params")
    fun getStrategyParams(): ResponseEntity<FifaDTO.FifaStrategyParamsResponse> {
        val (leagues, marketTypes, players, ruleTypes, matchupTypes, scopeTypes) = fifaStrategyService.getStrategyParams()
        val response =
            FifaDTO.FifaStrategyParamsResponse(leagues, marketTypes, players, ruleTypes, matchupTypes, scopeTypes)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/strategy/{strategyId}")
    fun getStrategy(@PathVariable strategyId: UUID): ResponseEntity<FifaDTO.FifaStrategyReadResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val strategy = fifaStrategyService.getStrategy(userEmail, strategyId)
        return ResponseEntity.status(HttpStatus.OK).body(strategy)
    }

    @GetMapping("/strategy")
    fun listAllStrategies(): ResponseEntity<FifaDTO.FifaStrategyListResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val strategies = fifaStrategyService.listAllStrategies(userEmail)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyListResponse(strategies))
    }

}