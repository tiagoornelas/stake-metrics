package com.stakemetrics.backend.domain.ports.fifa

import com.stakemetrics.backend.domain.entities.fifa.FifaLeague
import com.stakemetrics.backend.domain.entities.fifa.FifaMatch
import com.stakemetrics.backend.domain.entities.fifa.FifaPlayer
import java.util.Date

interface FifaMatchRepositoryPort {
    fun save(fifaMatch: FifaMatch)
    fun findLatestMatch(): FifaMatch?
    fun findByIntegrationId(integrationId: Int): FifaMatch?
    fun listFinishedMatchesByPlayerSince(league: FifaLeague, player: FifaPlayer, since: Date): List<FifaMatch>
    fun listLastFinishedMatchesByPlayer(league: FifaLeague, player: FifaPlayer, last: Int): List<FifaMatch>
    fun listFinishedMatchesByMatchupSince(
        league: FifaLeague, homePlayer: FifaPlayer, awayPlayer: FifaPlayer, since:
        Date
    ):
            List<FifaMatch>

    fun listLastFinishedMatchesByMatchup(
        league: FifaLeague, homePlayer: FifaPlayer, awayPlayer: FifaPlayer, last:
        Int
    ):
            List<FifaMatch>
}