package net.stakemetrics.persistence.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import net.stakemetrics.application.entities.AutoBettor
import net.stakemetrics.application.entities.enums.AutoBettorIntegrationStatus
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "auto_bettor")
data class AutoBettorModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    @OneToOne
    @JoinColumn(name = "user_id")
    val user: UserModel? = null,
    val integrationId: String? = null,
    val name: String? = null,
    val status: AutoBettorIntegrationStatus = AutoBettorIntegrationStatus.ACTIVE,
    val createdAt: Date = Date()
) {
    fun toDomain(): AutoBettor {
        return AutoBettor(
            id = id,
            user = user?.toDomain(),
            integrationId = integrationId,
            name= name,
            status= status,
            createdAt = createdAt
        )
    }
}

fun AutoBettor.toModel(): AutoBettorModel {
    return AutoBettorModel(
        id,
        user?.toModel(),
        integrationId,
        name,
        status,
        createdAt
    )
}