package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.DataDTO
import net.stakemetrics.application.service.FifaOddSnapshotService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/data")
class DataController(private val fifaOddSnapshotService: FifaOddSnapshotService) {

    @GetMapping("/fifa/snapshots/date/{date}")
    fun getFifaOddSnapshotsForDate(@PathVariable date: String): ResponseEntity<DataDTO.OddSnapshotResponse> {
        val oddSnapshots = fifaOddSnapshotService.getAllOddSnapshotsForDate(date)
        return ResponseEntity.ok(DataDTO.OddSnapshotResponse(oddSnapshots))
    }

}