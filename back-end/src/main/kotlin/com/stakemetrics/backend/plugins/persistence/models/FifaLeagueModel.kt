package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.FifaLeague
import com.stakemetrics.backend.domain.enums.FifaLeagueStatusTypes
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "fifa_leagues",
    uniqueConstraints = [UniqueConstraint(columnNames = ["integration_id"])],
    indexes = [Index(columnList = "id"), Index(columnList = "integration_id")]
)
data class FifaLeagueModel(
    @Id val id: UUID = UUID.randomUUID(),
    val integrationId: Int = 0,
    val status: FifaLeagueStatusTypes = FifaLeagueStatusTypes.ACTIVE,
    val name: String = "",
    val link: String = "",
    @OneToMany(mappedBy = "league", cascade = [CascadeType.ALL]) val matches: List<FifaMatchModel> = emptyList(),
    @OneToMany(mappedBy = "league", cascade = [CascadeType.ALL]) val players: List<FifaPlayerModel> = emptyList()
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