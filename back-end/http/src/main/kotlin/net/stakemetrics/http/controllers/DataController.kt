package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.DataDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.service.FifaOddSnapshotService
import net.stakemetrics.application.service.FifaStrategyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/data")
class DataController(
    private val fifaOddSnapshotService: FifaOddSnapshotService,
    private val fifaStrategyService: FifaStrategyService
) {

    @GetMapping("/fifa/snapshots/date/{date}")
    fun getFifaOddSnapshotsForDate(@PathVariable date: String): ResponseEntity<DataDTO.OddSnapshotResponse> {
        val oddSnapshots = fifaOddSnapshotService.getAllOddSnapshotsForDate(date)
        return ResponseEntity.ok(DataDTO.OddSnapshotResponse(oddSnapshots))
    }

    @GetMapping("/fifa/performance/strategies/leagues")
    fun getFifaStrategiesByLeaguePerformance(): ResponseEntity<DataDTO.FifaStrategiesByLeaguePerformanceResponse> {
        val strategies = fifaStrategyService.getAllStrategiesByLeaguePerformance()
        return ResponseEntity.ok(DataDTO.FifaStrategiesByLeaguePerformanceResponse(strategies))
    }

    @GetMapping("/fifa/strategy/{strategyId}")
    fun getPrivilegedFifaStrategy(@PathVariable strategyId: UUID): ResponseEntity<FifaStrategyDTO.FifaStrategyReadResponse> {
        val strategy = fifaStrategyService.getStrategyWithoutValidation(strategyId)
        return ResponseEntity.ok(strategy)
    }

}