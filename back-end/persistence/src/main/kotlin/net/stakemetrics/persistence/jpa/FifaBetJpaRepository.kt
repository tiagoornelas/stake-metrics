package net.stakemetrics.persistence.jpa

import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.MatchupTypes
import net.stakemetrics.application.entities.enums.StrategyScopeTypes
import net.stakemetrics.persistence.models.FifaBetModel
import net.stakemetrics.persistence.models.FifaMatchModel
import net.stakemetrics.persistence.models.FifaStrategyModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

interface FifaBetJpaRepository : JpaRepository<FifaBetModel, UUID> {

    interface MainStatisticsProjection {
        val numberOfBets: Long
        val profit: Double?
        val roi: Double?
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

    interface SimpleReportProjection {
        val betTime: Date?
        val matchTime: Date?
        val leagueName: String?
        val homeName: String?
        val awayName: String?
        val homeScore: Int?
        val awayScore: Int?
        val line: String?
        val handicap: Double?
        val odds: Double?
        val status: BetStatusTypes?
        val profit: Double?
    }

    interface TrendScopeAnalysisProjection {
        val id: UUID?
        val matchup: MatchupTypes?
        val type: StrategyScopeTypes?
        val totalMatches: Int?
        val homePlayerProbability: Double?
        val homePlayerFairLine: Double?
        val homePlayerJuice: Double?
        val drawProbability: Double?
        val drawFairLine: Double?
        val drawJuice: Double?
        val awayPlayerProbability: Double?
        val awayPlayerFairLine: Double?
        val awayPlayerJuice: Double?
        val overProbability: Double?
        val overFairLine: Double?
        val overJuice: Double?
        val underProbability: Double?
        val underFairLine: Double?
        val underJuice: Double?
    }

    interface DetailedReportRawProjection {
        val id: UUID
        val betTime: Date
        val matchTime: Date
        val leagueName: String
        val homeName: String
        val awayName: String
        val homeScore: Int?
        val awayScore: Int?
        val line: String
        val handicap: Double?
        val odds: Double
        val status: BetStatusTypes
        val profit: Double?
        val trendScopeAnalysis: TrendScopeAnalysisProjection?
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
        nativeQuery = true, value = """
        SELECT b.* FROM fifa_bets b
        JOIN messages m ON m.bet_id = b.id
        WHERE m.messenger_chat_id = :messengerChatId
        AND CONVERT_TZ(b.bet_time, '+00:00', :timezone) BETWEEN :startDate AND :endDate
        """
    )
    fun findByMessengerChatAndDateBetween(
        @Param("messengerChatId") messengerChatId: UUID,
        @Param("timezone") timezone: String,
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

    @Query(
        nativeQuery = true, value = """
        SELECT 
            DATE(CONVERT_TZ(b.bet_time, '+00:00', :timezone)) AS date,
            COALESCE(SUM(b.profit), 0.0) AS profit
        FROM fifa_bets b
        INNER JOIN fifa_strategies s ON b.strategy_id = s.id
        WHERE s.user_id = :userId 
            AND b.is_paper_bet = FALSE
            AND DATE(CONVERT_TZ(b.bet_time, '+00:00', :timezone)) BETWEEN :startDate AND :endDate
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

    @Query(
        nativeQuery = true, value = """
        SELECT 
            DATE_FORMAT(CONVERT_TZ(b.bet_time, '+00:00', :timezone), '%Y-%m-01') AS startDate,
            LAST_DAY(CONVERT_TZ(b.bet_time, '+00:00', :timezone)) AS endDate,
            COALESCE(SUM(b.profit), 0.0) AS profit
        FROM fifa_bets b
        INNER JOIN fifa_strategies s ON b.strategy_id = s.id
        WHERE s.user_id = :userId 
            AND b.is_paper_bet = FALSE
            AND DATE(CONVERT_TZ(b.bet_time, '+00:00', :timezone)) BETWEEN :startDate AND :endDate
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
        nativeQuery = true, value = """
        SELECT 
            COUNT(b.id) AS numberOfBets,
            COALESCE(SUM(b.profit), 0.0) AS profit,
            CASE 
                WHEN COUNT(b.id) = 0 THEN 0
                ELSE COALESCE(SUM(b.profit), 0.0) / COUNT(b.id)
            END AS roi
        FROM fifa_bets b
        INNER JOIN fifa_strategies s ON b.strategy_id = s.id
        WHERE s.user_id = :userId 
        AND CONVERT_TZ(b.bet_time, '+00:00', :timezone) BETWEEN :startDate AND :endDate 
        AND b.is_paper_bet = FALSE
        """
    )
    fun getMainStatisticsByUserAndDateBetween(
        @Param("userId") userId: UUID,
        @Param("timezone") timezone: String,
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

    @EntityGraph(attributePaths = ["match", "match.league", "match.home", "match.away"])
    @Query(
        """
        SELECT 
            b.betTime as betTime,
            m.time as matchTime,
            m.league.name as leagueName,
            m.home.name as homeName,
            m.away.name as awayName,
            m.homeGoalsAtFullTime as homeScore,
            m.awayGoalsAtFullTime as awayScore,
            b.line as line,
            b.handicap as handicap,
            b.odds as odds,
            b.status as status,
            b.profit as profit
        FROM FifaBetModel b 
        LEFT JOIN b.match m
        WHERE b.strategy.id = :strategyId 
        AND b.betTime > :betTime
    """
    )
    fun findSimpleReportBets(
        @Param("strategyId") strategyId: UUID,
        @Param("betTime") betTime: Date
    ): List<SimpleReportProjection>

    @Query(
        """
        SELECT DISTINCT
            b.id as id,
            b.betTime as betTime,
            m.time as matchTime,
            m.league.name as leagueName,
            m.home.name as homeName,
            m.away.name as awayName,
            m.homeGoalsAtFullTime as homeScore,
            m.awayGoalsAtFullTime as awayScore,
            b.line as line,
            b.handicap as handicap,
            b.odds as odds,
            b.status as status,
            b.profit as profit,
            tsa as trendScopeAnalysis
        FROM FifaBetModel b 
        LEFT JOIN b.match m
        LEFT JOIN FifaOddSnapshotModel os ON os.id = b.oddSnapshotId
        LEFT JOIN os.trendScopeAnalysis tsa
        WHERE b.strategy.id = :strategyId 
        AND b.betTime > :betTime
        AND EXISTS (
            SELECT 1 FROM FifaStrategyModel s2
            JOIN s2.scopes scope
            WHERE s2.id = b.strategy.id
            AND (tsa.matchup = scope.matchup OR scope.matchup IS NULL)
            AND (tsa.type = scope.type OR scope.type IS NULL)
        )
    """
    )
    fun findDetailedReportBets(
        @Param("strategyId") strategyId: UUID,
        @Param("betTime") betTime: Date
    ): List<DetailedReportRawProjection>
}