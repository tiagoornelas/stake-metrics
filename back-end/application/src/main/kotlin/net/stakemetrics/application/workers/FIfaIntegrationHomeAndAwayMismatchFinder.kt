package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.utils.Logger
import org.springframework.stereotype.Component

@Component
class FIfaIntegrationHomeAndAwayMismatchFinder(private val logger: Logger) {

    fun checkForHomeAndAwaySwappedByIntegration(fifaMatch: FifaMatch, odd: FifaDataSourceDTO.FifaOddRequest): Boolean {
        val result = checkForHomeAndAwaySwapped(odd.homePlayerName, odd.awayPlayerName, fifaMatch.home?.name, fifaMatch.away?.name)
        if (result) {
            logger.log("[Match ${fifaMatch.integrationId}] Swapped Home and Away for match ${fifaMatch.home?.name} vs ${fifaMatch.away?.name}")
        }
        return result
    }

    fun checkForHomeAndAwaySwappedByIntegration(fifaMatch: FifaMatch, request: FifaDataSourceDTO.FifaMatchRequest): Boolean {
        val result = checkForHomeAndAwaySwapped(request.home, request.away, fifaMatch.home?.name, fifaMatch.away?.name)
        if (result) {
            logger.log("[Match ${fifaMatch.integrationId}] Swapped Home and Away for match ${fifaMatch.home?.name} vs ${fifaMatch.away?.name}")
        }
        return result
    }

    private fun checkForHomeAndAwaySwapped(home1: String?, away1: String?, home2: String?, away2: String?): Boolean {
        return if (home1.equals(home2, true) && away1.equals(away2, true)) {
            false
        } else if (away1.equals(home2, true) && home1.equals(away2, true)) {
            true
        } else {
            throw Exception("Complete player mismatch: $home1 vs $away1 / $home2 vs $away2")
        }
    }
}