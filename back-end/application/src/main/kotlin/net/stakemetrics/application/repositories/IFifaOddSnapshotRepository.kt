package net.stakemetrics.application.repositories

import net.stakemetrics.application.entities.FifaLeague
import java.util.UUID
import net.stakemetrics.application.entities.FifaOddSnapshot
import java.util.Date

interface IFifaOddSnapshotRepository {
    fun save(fifaOddSnapshot: FifaOddSnapshot)
    fun findById(id: UUID): FifaOddSnapshot
    fun findAllPending(): List<FifaOddSnapshot>
    fun findAllByFifaMatchId(fifaMatchId: UUID): List<FifaOddSnapshot>
    fun deleteAllByFifaMatchId(fifaMatchId: UUID)
    fun findAllClosedByCreatedAtBetween(dateStart: Date, dateEnd: Date): List<FifaOddSnapshot>
    fun getLeagueGoalsTrendForLeagueAtInterval(league: FifaLeague, daysOffset: Int): List<Int>
}