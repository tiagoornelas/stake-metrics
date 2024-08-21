package net.stakemetrics.application.workers

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import org.springframework.stereotype.Component

@Component
class FifaBetMessageBuilder {

    fun build(fifaBet: FifaBet): String {
        val homePlayerName = fifaBet.match?.home?.name
        val awayPlayerName = fifaBet.match?.away?.name
        val line = parseLine(fifaBet)
        val odd = fifaBet.odds
        val leagueName = fifaBet.match?.league?.name
        val leagueLink = fifaBet.match?.league?.link
        val matchTime: String? = formatMatchTime(fifaBet)

        return """
            |📅 $homePlayerName x $awayPlayerName às $matchTime
            |
            |🔭 $line @ $odd
            |
            |🏆 $leagueName
            |
            |$leagueLink
        """.trimMargin()
    }

    private fun formatMatchTime(fifaBet: FifaBet): String? {
        return try {
            fifaBet.match?.time?.let { date ->
                val localTime = LocalTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault())
                localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
        } catch (e: Exception) {
            fifaBet.match?.time.toString()
        }
    }

    private fun parseLine(fifaBet: FifaBet): String {
        return when (fifaBet.line) {
            FifaMarketBetCandidates.HOME -> fifaBet.match?.home?.name ?: "Time da casa"
            FifaMarketBetCandidates.DRAW -> "Empate"
            FifaMarketBetCandidates.AWAY -> fifaBet.match?.away?.name ?: "Time visitante"
            FifaMarketBetCandidates.OVER -> "Mais de ${parseHandicap(fifaBet.handicap!!)} gols"
            FifaMarketBetCandidates.UNDER -> "Menos de ${parseHandicap(fifaBet.handicap!!)} gols"
        }
    }

    private fun parseHandicap(handicap: Double): String {
        return when {
            handicap % 1 == 0.25 -> {
                val base = handicap - 0.25
                String.format("%.1f, %.1f", base, base + 0.5)
            }

            handicap % 1 == 0.75 -> {
                val base = handicap - 0.75
                String.format("%.1f, %.1f", base + 0.5, base + 1.0)
            }

            else -> handicap.toString()
        }
    }
}