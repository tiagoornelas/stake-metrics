package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.application.entities.enums.OddSnapshotTypes
import net.stakemetrics.persistence.models.FifaOddSnapshotModel
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Date

interface FifaOddSnapshotJpaRepository : JpaRepository<FifaOddSnapshotModel, UUID> {
    fun findAllByFifaMatchId(fifaMatchId: UUID): List<FifaOddSnapshotModel>
    fun findAllByStatus(status: OddSnapshotTypes): List<FifaOddSnapshotModel>
    fun deleteAllByFifaMatchId(fifaMatchId: UUID)
    fun findAllByStatusAndCreatedAtBetween(status: OddSnapshotTypes, dateStart: Date, dateEnd: Date): List<FifaOddSnapshotModel>
}