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
import java.time.LocalDate
import java.time.LocalDateTime

interface FifaBetJpaRepository : JpaRepository<FifaBetModel, UUID> {

    interface MainStatisticsProjection {
        val numberOfBets: Long
        val profit: Double
        val roi: Double
    }

    interface DailyProfitProjection {
        val date: LocalDate
        val profit: Double
    }

    interface MonthlyProfitProjection {
        val startDate: LocalDate
        val endDate: LocalDate
        val profit: Double
    }

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

    @Query(
        """
        SELECT b FROM FifaBetModel b
        JOIN b.messages m
        WHERE m.messengerChat.id = :messengerChatId
        AND b.betTime BETWEEN :startDate AND :endDate
        """
    )
    fun findByMessengerChatAndDateBetween(
        @Param("messengerChatId") messengerChatId: UUID,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<FifaBetModel>

    @Query(
        """
        SELECT COUNT(b.id) FROM FifaBetModel b
        WHERE b.status = :status AND b.strategy.user.id = :userId AND b.isPaperBet = FALSE
        """
    )
    fun countByStatusAndUserId(@Param("status") status: BetStatusTypes, @Param("userId") userId: UUID): Int

    @Query(nativeQuery = true, value = """
        SELECT 
            DATE(CONVERT_TZ(b.bet_time, 'UTC', :timezone)) AS date,
            COALESCE(SUM(b.profit), 0.0) AS profit
        FROM fifa_bets b
        INNER JOIN fifa_strategies s ON b.strategy_id = s.id
        WHERE s.user_id = :userId 
            AND b.is_paper_bet = FALSE
            AND DATE(CONVERT_TZ(b.bet_time, 'UTC', :timezone)) BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date ASC
    """
    )
    fun getDailyProfits(
        @Param("userId") userId: UUID,
        @Param("timezone") timezone: String,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<DailyProfitProjection>

    @Query(nativeQuery = true, value = """
        SELECT 
            DATE_FORMAT(CONVERT_TZ(b.bet_time, 'UTC', :timezone), '%Y-%m-01') AS startDate,
            LAST_DAY(CONVERT_TZ(b.bet_time, 'UTC', :timezone)) AS endDate,
            COALESCE(SUM(b.profit), 0.0) AS profit
        FROM fifa_bets b
        INNER JOIN fifa_strategies s ON b.strategy_id = s.id
        WHERE s.user_id = :userId 
            AND b.is_paper_bet = FALSE
            AND DATE(CONVERT_TZ(b.bet_time, 'UTC', :timezone)) BETWEEN :startDate AND :endDate
        GROUP BY startDate, endDate
        ORDER BY startDate ASC
    """
    )
    fun getMonthlyProfits(
        @Param("userId") userId: UUID,
        @Param("timezone") timezone: String,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<MonthlyProfitProjection>

    @Query(
        """
        SELECT 
            COUNT(b.id) AS numberOfBets,
            SUM(b.profit) AS profit,
            CASE 
                WHEN COUNT(b.id) = 0 THEN 0
                ELSE SUM(b.profit) / COUNT(b.id)
            END AS roi
        FROM FifaBetModel b
        WHERE b.strategy.user.id = :userId AND b.betTime BETWEEN :startDate AND :endDate AND b.isPaperBet = FALSE
        """
    )
    fun getMainStatisticsByUserAndDateBetween(
        @Param("userId") userId: UUID,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): MainStatisticsProjection

    @Query(
        """
        SELECT 
            COALESCE(SUM(b.odds - 1), 0.0) AS possibleProfit
        FROM FifaBetModel b
        WHERE b.strategy.user.id = :userId 
        AND b.status = 0 
        AND b.profit IS NULL 
        AND b.isPaperBet = FALSE
        """
    )
    fun getPossibleProfitFromOpenBets(@Param("userId") userId: UUID): Double
}