package net.stakemetrics.persistence.repositories

import net.stakemetrics.application.entities.FifaLeague
import java.util.UUID
import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.enums.OddSnapshotTypes
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IFifaOddSnapshotRepository
import net.stakemetrics.persistence.jpa.FifaOddSnapshotJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository
import java.util.Date

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

    override fun findAllClosedByCreatedAtBetween(dateStart: Date, dateEnd: Date): List<FifaOddSnapshot> {
        return fifaOddSnapshotJpaRepository.findAllByStatusAndCreatedAtBetween(
            OddSnapshotTypes.CLOSED,
            dateStart,
            dateEnd
        ).map { it.toDomain() }
    }

    override fun getLeagueGoalsTrendForLeagueAtInterval(league: FifaLeague, daysOffset: Int): List<Int> {
        return fifaOddSnapshotJpaRepository.findCumulativeGoalsTrendForLeagueAtInterval(league.id, daysOffset)
    }
}