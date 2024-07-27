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
    fun saveStrategy(@RequestBody request: FifaDTO.FifaStrategyRequest): ResponseEntity<FifaDTO.FifaStrategyResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaServicePort.saveStrategy(userEmail, request)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyResponse())
    }

    @PutMapping("/strategy/status/{strategyId}")
    fun updateStrategyStatus(
        @RequestBody request: FifaDTO.FifaStrategyStatusRequest,
        @PathVariable strategyId: UUID
    ): ResponseEntity<FifaDTO.FifaStrategyResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaServicePort.updateStrategyStatus(userEmail, strategyId, request.status)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyResponse())
    }

    @DeleteMapping("/strategy/{strategyId}")
    fun deleteStrategy(@PathVariable strategyId: UUID): ResponseEntity<FifaDTO.FifaStrategyResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaServicePort.deleteStrategy(userEmail, strategyId)
        return ResponseEntity.status(HttpStatus.OK).body(FifaDTO.FifaStrategyResponse())
    }

}