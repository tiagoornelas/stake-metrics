package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.utils.Logger
import org.springframework.stereotype.Component

@Component
class FIfaIntegrationHomeAndAwayMismatchFinder(private val logger: Logger) {

    fun checkForHomeAndAwaySwappedByIntegration(fifaMatch: FifaMatch, odd: FifaDataSourceDTO.FifaOddRequest): Boolean {
        if (odd.homePlayerName == fifaMatch.home?.name && odd.awayPlayerName == fifaMatch.away?.name) {
            return false
        } else if (odd.awayPlayerName == fifaMatch.home?.name && odd.homePlayerName == fifaMatch.away?.name) {
            logger.log("[Match ${fifaMatch.integrationId}] Swapped Home and Away for match ${fifaMatch.home.name} vs ${fifaMatch.away.name}")
            return true
        } else {
            throw Exception("Complete player mismatch for match ${fifaMatch.integrationId}: ${odd.homePlayerName} vs ${odd.awayPlayerName} / ${fifaMatch.home?.name} vs ${fifaMatch.away?.name}")
        }
    }

    fun checkForHomeAndAwaySwappedByIntegration(fifaMatch: FifaMatch, request: FifaDataSourceDTO.FifaMatchRequest): Boolean {
        if (request.home == fifaMatch.home?.name && request.away == fifaMatch.away?.name) {
            return false
        } else if (request.away == fifaMatch.home?.name && request.home == fifaMatch.away?.name) {
            logger.log("[Match ${fifaMatch.integrationId}] Swapped Home and Away for match ${fifaMatch.home.name} vs ${fifaMatch.away.name}")
            return true
        } else {
            throw throw Exception("Complete player mismatch for match ${fifaMatch.integrationId}: ${request.home} vs ${request.away} / ${fifaMatch.home?.name} vs ${fifaMatch.away?.name}")
        }
    }

}