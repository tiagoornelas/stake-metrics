package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.enums.OddSnapshotTypes
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IFifaOddSnapshotRepository
import net.stakemetrics.persistence.jpa.FifaOddSnapshotJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class FifaOddSnapshotRepository(
    private val fifaOddSnapshotJpaRepository: FifaOddSnapshotJpaRepository
) : IFifaOddSnapshotRepository {

    override fun save(fifaOddSnapshot: FifaOddSnapshot) {
        fifaOddSnapshotJpaRepository.save(fifaOddSnapshot.toModel())
    }

    override fun findById(id: UUID): FifaOddSnapshot {
        return fifaOddSnapshotJpaRepository.findById(id)
            .orElseThrow { NotFoundException("FifaOddSnapshot", "id", id.toString()) }.toDomain()
    }

    override fun findAllPending(): List<FifaOddSnapshot> {
        return fifaOddSnapshotJpaRepository.findAllByStatus(OddSnapshotTypes.PENDING).map { it.toDomain() }
    }

    override fun findAllByFifaMatchId(fifaMatchId: UUID): List<FifaOddSnapshot> {
        return fifaOddSnapshotJpaRepository.findAllByFifaMatchId(fifaMatchId).map { it.toDomain() }
    }

    override fun deleteAllByFifaMatchId(fifaMatchId: UUID) {
        return fifaOddSnapshotJpaRepository.deleteAllByFifaMatchId(fifaMatchId)
    }
}