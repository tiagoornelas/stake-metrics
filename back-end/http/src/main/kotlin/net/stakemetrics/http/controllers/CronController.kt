package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.CronDTO
import net.stakemetrics.application.service.CronService
import net.stakemetrics.application.utils.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cron")
class CronController(private val logger: Logger, val cronService: CronService) {

    @PostMapping("/fifa/mine-results")
    fun mineFifaMatchResults(): ResponseEntity<CronDTO.Response> {
        return runCronAndReturnResponse("mineFifaMatchResults") { cronService.mineFifaMatchResults() }
    }

    @PostMapping("/fifa/mine-odds")
    fun mineFifaMatchOdds(): ResponseEntity<CronDTO.Response> {
        return runCronAndReturnResponse("mineFifaMatchOdds") { cronService.mineFifaMatchOdds() }
    }

    private fun runCronAndReturnResponse(methodName: String, block: () -> Unit): ResponseEntity<CronDTO.Response> {
        val elapsedTime = measureElapsedTimeInSeconds(methodName, block)
        return ResponseEntity.status(HttpStatus.OK).body(CronDTO.Response(elapsedTimeInSeconds = elapsedTime))
    }

    private fun measureElapsedTimeInSeconds(methodName: String, block: () -> Unit): Double {
        val startTime = System.currentTimeMillis()
        block()
        val endTime = System.currentTimeMillis()
        val elapsedTime = (endTime - startTime) / 1000.0
        logger.log("[CRON] Elapsed time for $methodName: $elapsedTime seconds")
        return elapsedTime
    }
}