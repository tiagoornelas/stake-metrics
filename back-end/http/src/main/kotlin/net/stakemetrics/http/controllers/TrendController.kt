package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.TrendDTO
import net.stakemetrics.application.entities.enums.DateIntervalTypes
import net.stakemetrics.application.service.FifaOddSnapshotService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/trend")
class TrendController(private val fifaOddSnapshotService: FifaOddSnapshotService) {

    @GetMapping("/fifa/goals/{dateInterval}")
    fun getFifaOddSnapshotsTrendForGoals(@PathVariable dateInterval: DateIntervalTypes): ResponseEntity<TrendDTO.FifaGoalsTrendResponse> {
        val leagueGoalsTrends = fifaOddSnapshotService.getAllLeaguesGoalsTrend(dateInterval)
        return ResponseEntity.ok(TrendDTO.FifaGoalsTrendResponse(leagueGoalsTrends))
    }

}