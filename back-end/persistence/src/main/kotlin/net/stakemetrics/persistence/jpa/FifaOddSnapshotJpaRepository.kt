package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.application.entities.enums.OddSnapshotTypes
import net.stakemetrics.persistence.models.FifaOddSnapshotModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Date

interface FifaOddSnapshotJpaRepository : JpaRepository<FifaOddSnapshotModel, UUID> {
    fun findAllByFifaMatchId(fifaMatchId: UUID): List<FifaOddSnapshotModel>
    fun findAllByStatus(status: OddSnapshotTypes): List<FifaOddSnapshotModel>
    fun deleteAllByFifaMatchId(fifaMatchId: UUID)
    fun findAllByStatusAndCreatedAtBetween(status: OddSnapshotTypes, dateStart: Date, dateEnd: Date): List<FifaOddSnapshotModel>

    @Query(
        """
    SELECT
        SUM(CASE WHEN over_profit > 0 THEN 1 WHEN over_profit < 0 THEN -1 ELSE 0 END) 
                    OVER (
        ORDER BY created_at ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as cumulative_over_profit
    FROM
        fifa_odd_snapshot fos
    INNER JOIN fifa_matches fm ON
        fm.id = fos.match_id
    WHERE
        fm.league_id = :league_id
        AND created_at >= NOW() - INTERVAL :days_offset DAY
    ORDER BY
        created_at;
""", nativeQuery = true
    )
    fun findCumulativeGoalsTrendForLeagueAtInterval(@Param("league_id") leagueId: UUID, @Param("days_offset") daysOffset: Int): List<Int>
}