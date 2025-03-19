package net.stakemetrics.application.entities

import net.stakemetrics.application.entities.enums.AutoBettorIntegrationStatus
import java.util.Date
import java.util.UUID

data class AutoBettor(
    val id: UUID = UUID.randomUUID(),
    val user: User? = null,
    var name: String? = null,
    val integrationId: String? = null,
    var status: AutoBettorIntegrationStatus = AutoBettorIntegrationStatus.ACTIVE,
    val createdAt: Date = Date()
) {
    fun isActive(): Boolean {
        return status == AutoBettorIntegrationStatus.ACTIVE
    }
}