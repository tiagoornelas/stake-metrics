package net.stakemetrics.persistence.models

import jakarta.persistence.*
import java.util.UUID
import net.stakemetrics.application.entities.FifaStrategy
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
                COALESCE(SUM(IF(b.profit IS NOT NULL AND b.is_paper_bet = false, b.profit, 0)), 0) AS activeResult,
                COALESCE(SUM(IF(b.profit IS NOT NULL AND b.is_paper_bet = false, b.profit, 0)) / NULLIF(COUNT(CASE WHEN b.profit IS NOT NULL AND b.is_paper_bet = false THEN 1 END), 0), 0) AS activeRoi,
                COALESCE(SUM(IF(b.profit IS NOT NULL AND DATE(CONVERT_TZ(b.bet_time, '+00:00', '-03:00')) = CURDATE(), b.profit, 0)), 0) AS todaysResult,
                FLOOR(
                    COALESCE(COUNT(CASE WHEN b.profit IS NOT NULL THEN 1 END), 0) / 
                    NULLIF(COUNT(DISTINCT DATE(CONVERT_TZ(b.bet_time, '+00:00', '-03:00'))), 0)
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
            COALESCE(activeResult, 0) AS activeResult, 
            COALESCE(activeRoi, 0) AS activeRoi, 
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
            ColumnResult(name = "activeResult", type = Double::class),
            ColumnResult(name = "activeRoi", type = Double::class),
            ColumnResult(name = "todaysResult", type = Double::class),
            ColumnResult(name = "averageDailyBets", type = Int::class)
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