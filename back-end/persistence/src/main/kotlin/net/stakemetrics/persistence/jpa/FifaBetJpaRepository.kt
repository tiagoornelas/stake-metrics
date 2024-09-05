package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.persistence.models.FifaBetModel
import net.stakemetrics.persistence.models.FifaMatchModel
import net.stakemetrics.persistence.models.FifaStrategyModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FifaBetJpaRepository : JpaRepository<FifaBetModel, UUID> {
    fun existsByStrategyAndMatch(strategy: FifaStrategyModel, match: FifaMatchModel): Boolean
    fun deleteAllByStrategyId(strategyId: UUID)

    @Query(
        """
        SELECT b FROM FifaBetModel b
        WHERE b.strategy.id = :strategyId
        AND (:#{#betFilter.closedBets} = true OR (b.profit IS NULL AND b.status = 0))
        AND (:#{#betFilter.openBets} = true OR (b.profit IS NOT NULL AND b.status != 0))
        AND (:#{#betFilter.realBets} = true OR b.isPaperBet = true)
        AND (:#{#betFilter.paperBets} = true OR b.isPaperBet = false)
        AND (:#{#betFilter.league.isEmpty()} = true OR b.match.league.id IN :#{#betFilter.league})
        ORDER BY b.match.time DESC
        """
    )
    fun findBetsByStrategyAndFilter(
        @Param("strategyId") strategyId: UUID,
        @Param("betFilter") betFilter: FifaBetDTO.BetFilter,
        pageable: Pageable
    ): Page<FifaBetModel>

    fun findAllByStatusOrProfit(status: BetStatusTypes, profit: Double?): List<FifaBetModel>

    @Query(
        """
        SELECT ROUND(SUM(b.profit) OVER (ORDER BY b.bet_time ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW), 2) AS cumulativeProfit
        FROM fifa_bets b
        WHERE b.strategy_id = :strategyId AND b.profit IS NOT NULL
        ORDER BY b.bet_time
    """, nativeQuery = true
    )
    fun findCumulativeProfitsByStrategyId(@Param("strategyId") strategyId: UUID): List<Double>
}