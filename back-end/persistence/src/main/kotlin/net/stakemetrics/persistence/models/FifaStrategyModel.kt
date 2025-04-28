package net.stakemetrics.persistence.models

import jakarta.persistence.*
import java.util.UUID
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.dtos.DataDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.enums.FifaStrategyStatus

@Entity
@Table(name = "fifa_strategies")
@NamedNativeQuery(
    name = "find_strategy_statistics_by_user_id",
    query = """
        WITH strategy_stats AS (
            SELECT
                s.id AS id,
                s.name AS name,
                s.status AS status,
                COUNT(CASE WHEN b.profit IS NULL AND b.status = 0 THEN 1 END) AS openBets,
                COUNT(CASE WHEN b.profit IS NOT NULL THEN 1 END) AS bets,
                COALESCE(SUM(b.profit), 0) AS result,
                COALESCE(SUM(b.profit) / NULLIF(COUNT(CASE WHEN b.profit IS NOT NULL THEN 1 END), 0), 0) AS roi,
                COALESCE(SUM(IF(b.profit IS NOT NULL
                    AND DATE(CONVERT_TZ(b.bet_time, '+00:00', :timezone)) = DATE(CONVERT_TZ(NOW(), '+00:00', :timezone)), b.profit, 0)), 0) AS todaysResult,
                FLOOR(
                    COALESCE(COUNT(CASE WHEN b.profit IS NOT NULL THEN 1 END), 0) /
                    NULLIF(COUNT(DISTINCT DATE(CONVERT_TZ(b.bet_time, '+00:00', :timezone))), 0)
                ) AS averageDailyBets
            FROM fifa_strategies s
            LEFT JOIN fifa_bets b ON s.id = b.strategy_id
            WHERE s.user_id = :userId
            GROUP BY s.id, s.name, s.status
        )
        SELECT
            id,
            name,
            status,
            COALESCE(openBets, 0) AS openBets,
            COALESCE(bets, 0) AS bets,
            COALESCE(result, 0) AS result,
            COALESCE(roi, 0) AS roi,
            COALESCE(todaysResult, 0) AS todaysResult,
            COALESCE(averageDailyBets, 0) AS averageDailyBets
        FROM strategy_stats
        """,
    resultSetMapping = "fifa_strategy_statistic_single_response"
)
@SqlResultSetMapping(
    name = "fifa_strategy_statistic_single_response",
    classes = [ConstructorResult(
        targetClass = FifaStrategyDTO.FifaStrategyStatisticSingleResponse::class,
        columns = [
            ColumnResult(name = "id", type = UUID::class),
            ColumnResult(name = "name", type = String::class),
            ColumnResult(name = "status", type = FifaStrategyStatus::class),
            ColumnResult(name = "openBets", type = Int::class),
            ColumnResult(name = "bets", type = Int::class),
            ColumnResult(name = "result", type = Double::class),
            ColumnResult(name = "roi", type = Double::class),
            ColumnResult(name = "todaysResult", type = Double::class),
            ColumnResult(name = "averageDailyBets", type = Int::class)
        ]
    )]
)
@NamedNativeQuery(
    name = "find_strategies_by_league_performance",
    query = """
        SELECT
            fs.id AS strategyId,
            fl.name AS leagueName,
            fl.id AS leagueId,
            u.email AS userLogin,
            SUM(CASE WHEN fb.bet_time >= DATE_SUB(NOW(), INTERVAL 2 HOUR) THEN fb.profit ELSE 0 END) AS profitLastTwoHours,
            SUM(CASE WHEN fb.bet_time >= DATE_SUB(NOW(), INTERVAL 4 HOUR) THEN fb.profit ELSE 0 END) AS profitLastFourHours,
            SUM(CASE WHEN fb.bet_time >= DATE_SUB(NOW(), INTERVAL 6 HOUR) THEN fb.profit ELSE 0 END) AS profitLastSixHours,
            SUM(CASE WHEN fb.bet_time >= DATE_SUB(NOW(), INTERVAL 12 HOUR) THEN fb.profit ELSE 0 END) AS profitLastTwelveHours,
            SUM(CASE WHEN fb.bet_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) THEN fb.profit ELSE 0 END) AS profitLastTwentyFourHours,
            SUM(fb.profit) AS profitLastWeek
        FROM
            fifa_bets fb
        INNER JOIN
            fifa_strategies fs ON fb.strategy_id = fs.id
        INNER JOIN
            users u ON fs.user_id = u.id
        INNER JOIN
            fifa_matches fm ON fb.match_id = fm.id
        INNER JOIN
            fifa_leagues fl ON fl.id = fm.league_id
        WHERE
            fb.bet_time >= DATE_SUB(NOW(), INTERVAL 1 WEEK)
        GROUP BY
            fs.id, fl.name, u.email
        ORDER BY
            profitLastTwoHours DESC
    """,
    resultSetMapping = "fifa_strategy_league_performance_result"
)
@SqlResultSetMapping(
    name = "fifa_strategy_league_performance_result",
    classes = [ConstructorResult(
        targetClass = DataDTO.FifaStrategiesByLeaguePerformanceSingleResponse::class,
        columns = [
            ColumnResult(name = "strategyId", type = UUID::class),
            ColumnResult(name = "leagueName", type = String::class),
            ColumnResult(name = "leagueId", type = UUID::class),
            ColumnResult(name = "userLogin", type = String::class),
            ColumnResult(name = "profitLastTwoHours", type = Double::class),
            ColumnResult(name = "profitLastFourHours", type = Double::class),
            ColumnResult(name = "profitLastSixHours", type = Double::class),
            ColumnResult(name = "profitLastTwelveHours", type = Double::class),
            ColumnResult(name = "profitLastTwentyFourHours", type = Double::class),
            ColumnResult(name = "profitLastWeek", type = Double::class)
        ]
    )]
)
data class FifaStrategyModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val status: FifaStrategyStatus = FifaStrategyStatus.INACTIVE,
    val name: String = "",
    val marketType: FifaMarketTypes = FifaMarketTypes.MATCH_ODDS,
    @ElementCollection
    @CollectionTable(name = "fifa_strategy_market_subtypes", joinColumns = [JoinColumn(name = "strategy_id")])
    @Column(name = "market_subtype")
    val marketSubTypes: MutableSet<FifaMarketSubTypes> = mutableSetOf(),
    @ManyToMany
    @JoinTable(
        name = "fifa_strategy_leagues",
        joinColumns = [JoinColumn(name = "strategy_id")],
        inverseJoinColumns = [JoinColumn(name = "league_id")]
    )
    val leagues: MutableSet<FifaLeagueModel> = mutableSetOf(),
    @ManyToMany
    @JoinTable(
        name = "fifa_strategy_excluded_players",
        joinColumns = [JoinColumn(name = "strategy_id")],
        inverseJoinColumns = [JoinColumn(name = "player_id")]
    )
    val excludedPlayers: MutableSet<FifaPlayerModel> = mutableSetOf(),
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "strategy_id")
    var scopes: MutableSet<FifaStrategyScopeModel> = mutableSetOf(),
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: UserModel? = null
) {
    fun toDomain(): FifaStrategy {
        return FifaStrategy(
            id,
            status,
            name,
            marketType,
            marketSubTypes,
            leagues.map { it.toDomain() }.toMutableSet(),
            excludedPlayers.map { it.toDomain() }.toMutableSet(),
            scopes.map { it.toDomain() }.toMutableSet(),
            user?.toDomain()
        )
    }
}

fun FifaStrategy.toModel(): FifaStrategyModel {
    return FifaStrategyModel(
        id = this.id,
        status = this.status,
        name = this.name,
        marketType = this.marketType,
        marketSubTypes = this.marketSubTypes,
        leagues = this.leagues.map { it.toModel() }.toMutableSet(),
        excludedPlayers = this.excludedPlayers.map { it.toModel() }.toMutableSet(),
        scopes = this.scopes.map { it.toModel() }.toMutableSet(),
        user = this.user?.toModel()
    )
}