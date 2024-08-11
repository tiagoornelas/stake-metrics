package net.stakemetrics.persistence.models

import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import jakarta.persistence.*
import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.FifaMatch

@Entity
@Table(
    name = "fifa_matches",
    uniqueConstraints = [UniqueConstraint(columnNames = ["integration_id"])],
    indexes = [Index(columnList = "id"), Index(columnList = "integration_id")]
)
data class FifaMatchModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val integrationId: Long = 0L,
    val time: Date = Date(),
    val status: FifaMatchStatusTypes = FifaMatchStatusTypes.NOT_STARTED,
    @ManyToOne @JoinColumn(name = "league_id")
    val league: FifaLeagueModel? = null,
    @ManyToOne @JoinColumn(name = "home_player_id")
    val home: FifaPlayerModel? = null,
    @ManyToOne @JoinColumn(name = "away_player_id")
    val away: FifaPlayerModel? = null,
    val homeGoalsAtHalfTime: Int? = null,
    val homeGoalsAtFullTime: Int? = null,
    val awayGoalsAtHalfTime: Int? = null,
    val awayGoalsAtFullTime: Int? = null,
    val totalGoalsAtHalfTime: Int? = null,
    val totalGoalsAtFullTime: Int? = null,
    @ManyToOne @JoinColumn(name = "winner_player_id")
    val winner: FifaPlayerModel? = null
) {
    fun toDomain(): FifaMatch {
        return FifaMatch(
            id,
            integrationId,
            time,
            status,
            league?.toDomain(),
            home?.toDomain(),
            away?.toDomain(),
            homeGoalsAtHalfTime,
            homeGoalsAtFullTime,
            awayGoalsAtHalfTime,
            awayGoalsAtFullTime,
            totalGoalsAtHalfTime,
            totalGoalsAtFullTime,
            winner?.toDomain()
        )
    }
}

fun FifaMatch.toModel(): FifaMatchModel {
    return FifaMatchModel(
        id,
        integrationId,
        time,
        status,
        league?.toModel(),
        home?.toModel(),
        away?.toModel(),
        homeGoalsAtHalfTime,
        homeGoalsAtFullTime,
        awayGoalsAtHalfTime,
        awayGoalsAtFullTime,
        totalGoalsAtHalfTime,
        totalGoalsAtFullTime,
        winner?.toModel()
    )
}