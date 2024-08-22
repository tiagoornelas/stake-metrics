package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.service.FifaBetService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.data.domain.Page

@RestController
@RequestMapping("/fifa/bet")
class FifaBetController(private val fifaBetService: FifaBetService) {

    @GetMapping
    fun listBets(
        @RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "30") size: Int
    ): ResponseEntity<Page<FifaBetDTO.BetResponse>> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val bets = fifaBetService.listBets(userEmail, page, size)
        return ResponseEntity.ok(bets)
    }
}