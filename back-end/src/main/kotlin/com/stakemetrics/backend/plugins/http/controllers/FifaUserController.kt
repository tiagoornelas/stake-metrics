package com.stakemetrics.backend.plugins.http.controllers

import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import com.stakemetrics.backend.plugins.http.ports.FifaServicePort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

}