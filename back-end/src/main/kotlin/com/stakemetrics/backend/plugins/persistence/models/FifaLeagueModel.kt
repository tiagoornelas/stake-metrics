package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.FifaLeague
import com.stakemetrics.backend.domain.enums.FifaLeagueStatusTypes
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
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