package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaOddSnapshot

interface IFifaOddSnapshotRepository {
    fun save(fifaOddSnapshot: FifaOddSnapshot)
    fun findById(id: UUID): FifaOddSnapshot
    fun findAllPending(): List<FifaOddSnapshot>
    fun findAllByFifaMatchId(fifaMatchId: UUID): List<FifaOddSnapshot>
    fun deleteAllByFifaMatchId(fifaMatchId: UUID)
}