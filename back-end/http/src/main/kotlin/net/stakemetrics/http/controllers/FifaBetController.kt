package net.stakemetrics.http.controllers

import java.util.UUID
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.service.FifaBetService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fifa/bet")
class FifaBetController(private val fifaBetService: FifaBetService) {

    @PostMapping("/strategy/{strategyId}")
    fun listBetsByStrategy(
        @PathVariable strategyId: UUID,
        @RequestBody betFilter: FifaBetDTO.BetFilter
    ): ResponseEntity<Page<FifaBetDTO.BetResponse>> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val bets = fifaBetService.listBets(userEmail, strategyId, betFilter)
        return ResponseEntity.ok(bets)
    }

    @DeleteMapping(("/{betId}"))
    fun deleteBet(@PathVariable betId: UUID): ResponseEntity<FifaBetDTO.DeleteResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        fifaBetService.delete(userEmail, betId)
        return ResponseEntity.ok(FifaBetDTO.DeleteResponse())
    }

    @GetMapping("/statistics")
    fun getStatistics(): ResponseEntity<FifaBetDTO.StatisticsResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val statistics = fifaBetService.getStatistics(userEmail)
        return ResponseEntity.ok(statistics)
    }
}