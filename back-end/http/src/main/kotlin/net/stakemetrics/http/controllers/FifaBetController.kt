package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.service.FifaBetService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/fifa/bet")
class FifaBetController(private val fifaBetService: FifaBetService) {

    @GetMapping
    fun listBets(): ResponseEntity<FifaBetDTO.BetListResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val bets = fifaBetService.listBets(userEmail)
        return ResponseEntity.ok(FifaBetDTO.BetListResponse(bets))
    }
}