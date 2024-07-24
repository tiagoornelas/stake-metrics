package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.fifa.FifaPlayer
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "fifa_players", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
data class FifaPlayerModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    @ManyToOne @JoinColumn(name = "league_id")
    val league: FifaLeagueModel? = null,
    @ManyToMany(mappedBy = "excludedPlayers")
    val excludedFromStrategies: MutableSet<FifaStrategyModel> = mutableSetOf()
) {
    fun toDomain(): FifaPlayer {
        return FifaPlayer(
            id,
            name,
            league?.toDomain()
        )
    }
}