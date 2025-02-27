package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import org.springframework.stereotype.Component
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Component
class FifaBetMessageBuilder {

    fun build(fifaBet: FifaBet): String {
        val commonMessage = buildCommonMessage(fifaBet)
        return """
            |$commonMessage
            |
            |${generateBetLink(fifaBet)}
        """.trimMargin()
    }

    fun buildResult(fifaBet: FifaBet): String {
        val commonMessage = buildCommonMessage(fifaBet)
        val score = "${fifaBet.match?.homeGoalsAtFullTime}x${fifaBet.match?.awayGoalsAtFullTime}"
        val result = when (fifaBet.status) {
            BetStatusTypes.PENDING -> "🕒 Pendente"
            BetStatusTypes.WON -> "✅ Vencida"
            BetStatusTypes.HALF_WON -> "✅\uD83D\uDD04 Meio Ganho"
            BetStatusTypes.VOID -> "\uD83D\uDD04 Devolvida"
            BetStatusTypes.HALF_LOST -> "❌\uD83D\uDD04 Meia Perca"
            BetStatusTypes.LOST -> "❌ Perdida"
        }
        return """
            |$commonMessage
            |
            |$result - Placar: $score
        """.trimMargin()
    }

    fun buildDiscard(fifaBet: FifaBet): String {
        val commonMessage = buildCommonMessage(fifaBet)
        return """
            |$commonMessage
            |
            |🗑️ Descartada
        """.trimMargin()
    }

    fun buildReport(fifaBets: List<FifaBet>, targetDate: LocalDateTime): String {
        val formattedDate = targetDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val title = "🏆 Relatório de Entradas - $formattedDate 🏆\n\n"

        val totalProfit = fifaBets.sumOf { it.profit ?: 0.0 }
        val totalBetsCount = fifaBets.size
        val totalROI = if (totalBetsCount > 0) (totalProfit / totalBetsCount * 100) else 0.0

        val leagueStats = fifaBets.groupBy { it.match?.league?.name ?: "Sem Liga" }
            .mapValues { (_, leagueBets) ->
                DetailedBetStats(
                    totalProfit = leagueBets.sumOf { it.profit ?: 0.0 },
                    betsCount = leagueBets.size,
                    roi = if (leagueBets.isNotEmpty())
                        (leagueBets.sumOf { it.profit ?: 0.0 } / leagueBets.size * 100)
                    else 0.0
                )
            }

        val marketStats = fifaBets.groupBy { getMarketType(it.line) }
            .mapValues { (_, marketBets) ->
                DetailedBetStats(
                    totalProfit = marketBets.sumOf { it.profit ?: 0.0 },
                    betsCount = marketBets.size,
                    roi = if (marketBets.isNotEmpty())
                        (marketBets.sumOf { it.profit ?: 0.0 } / marketBets.size * 100)
                    else 0.0
                )
            }

        val reportBuilder = StringBuilder()

        reportBuilder.append(title)

        reportBuilder.append("📊 Desempenho Geral:\n\n")
        reportBuilder.append("Lucro Total: ${decimalFormat.format(totalProfit)}u ${getProfitEmoji(totalProfit)}\n")
        reportBuilder.append("Total de Entradas: $totalBetsCount\n")
        reportBuilder.append("ROI Total: ${decimalFormat.format(totalROI)}%\n\n")

        if (leagueStats.isNotEmpty()) {
            reportBuilder.append("🏆 Desempenho por Liga:\n\n")
            leagueStats.forEach { (league, stats) ->
                reportBuilder.append("$league\n")
                reportBuilder.append("    Lucro: ${decimalFormat.format(stats.totalProfit)}u ${getProfitEmoji(stats.totalProfit)}\n")
                reportBuilder.append("    Entradas: ${stats.betsCount}\n")
                reportBuilder.append("    ROI: ${decimalFormat.format(stats.roi)}%\n\n")
            }
        }

        if (marketStats.isNotEmpty()) {
            reportBuilder.append("📈 Desempenho por Mercado:\n\n")
            marketStats.forEach { (market, stats) ->
                reportBuilder.append("$market\n")
                reportBuilder.append("    Lucro: ${decimalFormat.format(stats.totalProfit)}u ${getProfitEmoji(stats.totalProfit)}\n")
                reportBuilder.append("    Entradas: ${stats.betsCount}\n")
                reportBuilder.append("    ROI: ${decimalFormat.format(stats.roi)}%\n\n")
            }
        }

        return reportBuilder.toString().trimEnd()
    }

    data class DetailedBetStats(
        val totalProfit: Double,
        val betsCount: Int,
        val roi: Double
    )

    private fun buildCommonMessage(fifaBet: FifaBet): String {
        val homePlayerName = fifaBet.match?.home?.name
        val awayPlayerName = fifaBet.match?.away?.name
        val line = parseLine(fifaBet)
        val odd = fifaBet.odds
        val leagueName = fifaBet.match?.league?.name
        val matchTime: String? = formatMatchTime(fifaBet)

        return """
            |📅 $homePlayerName x $awayPlayerName às $matchTime
            |
            |🔭 $line @ $odd
            |
            |🏆 $leagueName
        """.trimMargin()
    }

    private fun formatMatchTime(fifaBet: FifaBet): String? {
        return try {
            fifaBet.match?.time?.let { date ->
                val localTime = LocalTime.ofInstant(date.toInstant(), java.time.ZoneId.of("UTC-3"))
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
            FifaMarketBetCandidates.ASIAN_OVER_GOALS -> "Mais de ${parseHandicap(fifaBet.handicap!!)} gols"
            FifaMarketBetCandidates.ASIAN_UNDER_GOALS -> "Menos de ${parseHandicap(fifaBet.handicap!!)} gols"
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

    private fun getMarketType(candidate: FifaMarketBetCandidates): String {
        return when (candidate) {
            FifaMarketBetCandidates.HOME,
            FifaMarketBetCandidates.DRAW,
            FifaMarketBetCandidates.AWAY -> "Mercado de Vencedor"

            FifaMarketBetCandidates.ASIAN_OVER_GOALS,
            FifaMarketBetCandidates.ASIAN_UNDER_GOALS -> "Mercado de Gols"
        }
    }

    private fun generateBetLink(fifaBet: FifaBet): String {
        val match = fifaBet.match
        return when {
            match?.bet365Id != null -> "https://www.bet365.bet.br/dl/sportsbookredirect?bet=1&bs=${match.bet365Id}-1~1"

            else -> match?.league?.link ?: ""
        }
    }

    private fun getProfitEmoji(profit: Double): String {
        return when {
            profit > 0 -> "✅"
            profit < 0 -> "❌"
            else -> "\uD83D\uDD04"
        }
    }

    private val decimalFormat = DecimalFormat("#,##0.00")
}