package net.stakemetrics.integration.betsapi.utils

import org.springframework.stereotype.Component

@Component
class FifaMarketHelper {

    fun getPlayerName(defaultName: String): String {
        return defaultName.split("(").last().split(")").first().trim()
    }

    fun getHandicap(handicapString: String): Double {
        return if (handicapString.contains(",")) {
            val parts = handicapString.split(",")
            (parts[0].toDouble() + parts[1].toDouble()) / 2
        } else {
            handicapString.toDouble()
        }
    }
}