package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.fifa.FifaStrategy
import com.stakemetrics.backend.domain.enums.fifa.FifaMarketSubTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaMarketTypes
import com.stakemetrics.backend.domain.enums.fifa.FifaStrategyStatus
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "fifa_strategies")
data class FifaStrategyModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val status: FifaStrategyStatus = FifaStrategyStatus.PAPER_BET,
    val name: String = "",
    val marketType: FifaMarketTypes = FifaMarketTypes.MATCH_ODDS,
    @ElementCollection
    @CollectionTable(name = "fifa_strategy_market_subtypes", joinColumns = [JoinColumn(name = "strategy_id")])
    @Column(name = "market_subtype")
    val marketSubTypes: MutableSet<FifaMarketSubTypes> = mutableSetOf(),
    @ManyToMany(cascade = [CascadeType.ALL])
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
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "strategy_id")
    var rules: MutableSet<FifaRuleModel> = mutableSetOf(),
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
            rules.map { it.toDomain() }.toMutableSet(),
            user?.toDomain()
        )
    }
}
