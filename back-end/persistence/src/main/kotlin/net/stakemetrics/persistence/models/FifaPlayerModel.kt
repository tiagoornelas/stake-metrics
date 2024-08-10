package net.stakemetrics.persistence.models

import jakarta.persistence.*
import java.util.UUID
import net.stakemetrics.application.entities.FifaPlayer

@Entity
@Table(name = "fifa_players", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
data class FifaPlayerModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    @ManyToOne @JoinColumn(name = "league_id")
    val league: FifaLeagueModel? = null
) {
    fun toDomain(): FifaPlayer {
        return FifaPlayer(
            id,
            name,
            league?.toDomain()
        )
    }
}

fun FifaPlayer.toModel(): FifaPlayerModel {
    return FifaPlayerModel(
        id,
        name,
        league?.toModel()
    )
}
