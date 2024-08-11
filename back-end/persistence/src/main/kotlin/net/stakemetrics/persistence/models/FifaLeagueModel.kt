package net.stakemetrics.persistence.models

import net.stakemetrics.application.entities.enums.FifaLeagueStatusTypes
import jakarta.persistence.*
import java.util.UUID
import net.stakemetrics.application.entities.FifaLeague

@Entity
@Table(
    name = "fifa_leagues",
    uniqueConstraints = [UniqueConstraint(columnNames = ["integration_id"])],
    indexes = [Index(columnList = "id"), Index(columnList = "integration_id")]
)
data class FifaLeagueModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val integrationId: Long = 0L,
    val status: FifaLeagueStatusTypes = FifaLeagueStatusTypes.ACTIVE,
    val name: String = "",
    val link: String = "",
    @OneToMany(mappedBy = "league")
    val players: MutableSet<FifaPlayerModel> = mutableSetOf(),
) {
    fun toDomain(): FifaLeague {
        return FifaLeague(
            id,
            integrationId,
            status,
            name,
            link
        )
    }
}

fun FifaLeague.toModel(): FifaLeagueModel {
    return FifaLeagueModel(id, integrationId, status, name, link)
}