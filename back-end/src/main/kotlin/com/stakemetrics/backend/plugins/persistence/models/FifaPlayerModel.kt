package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.FifaPlayer
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "fifa_players", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
data class FifaPlayerModel(
    @Id val id: UUID = UUID.randomUUID(),
    val name: String = "",
    @ManyToOne @JoinColumn(name = "league_id") val league: FifaLeagueModel? = null
) {
    fun toDomain(): FifaPlayer {
        return FifaPlayer(
            id,
            name,
            league?.toDomain()
        )
    }
}