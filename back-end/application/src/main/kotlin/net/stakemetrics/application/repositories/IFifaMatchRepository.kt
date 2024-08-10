package net.stakemetrics.application.repositories

import java.util.Date
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaPlayer

interface IFifaMatchRepository {
    fun save(fifaMatch: FifaMatch)
    fun findLatestMatch(): FifaMatch?
    fun findByIntegrationId(integrationId: Int): FifaMatch?
    fun listFinishedMatchesByPlayerSince(league: FifaLeague, player: FifaPlayer, since: Date): List<FifaMatch>
    fun listLastFinishedMatchesByPlayer(league: FifaLeague, player: FifaPlayer, last: Int): List<FifaMatch>
    fun listFinishedMatchesByMatchupSince(
        league: FifaLeague, homePlayer: FifaPlayer, awayPlayer: FifaPlayer, since:
        Date
    ): List<FifaMatch>

    fun listLastFinishedMatchesByMatchup(
        league: FifaLeague, homePlayer: FifaPlayer, awayPlayer: FifaPlayer, last:
        Int
    ): List<FifaMatch>
}