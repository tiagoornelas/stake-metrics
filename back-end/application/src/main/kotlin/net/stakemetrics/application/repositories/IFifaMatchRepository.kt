package net.stakemetrics.application.repositories

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaPlayer

interface IFifaMatchRepository {
    fun save(fifaMatch: FifaMatch)
    fun findById(id: UUID): FifaMatch?
    fun findLatestMatch(): FifaMatch?
    fun findLatestMatchForLeague(league: FifaLeague): FifaMatch?
    fun existsByIntegrationId(integrationId: Long): Boolean
    fun findByIntegrationId(integrationId: Long): FifaMatch?
    fun listFinishedMatchesByPlayerSince(league: FifaLeague, player: FifaPlayer, since: Date): List<FifaMatch>
    fun listFinishedMatchesByMatchupSince(
        league: FifaLeague, homePlayer: FifaPlayer,
        awayPlayer: FifaPlayer, since: Date
    ): List<FifaMatch>
}