package net.stakemetrics.persistence.models

import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import jakarta.persistence.*
import java.util.UUID
import net.stakemetrics.application.entities.FifaStrategy

@Entity
@Table(name = "fifa_strategies")
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