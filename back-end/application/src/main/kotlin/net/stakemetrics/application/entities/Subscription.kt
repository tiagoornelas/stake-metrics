package net.stakemetrics.application.entities

import net.stakemetrics.application.entities.enums.SubscriptionStatus
import java.util.Date
import java.util.UUID

data class Subscription(
    val id: UUID = UUID.randomUUID(),
    val user: User? = null,
    val integrationId: String = "",
    val subscriptionId: String? = null,
    val status: SubscriptionStatus = SubscriptionStatus.INACTIVE,
    val expiresAt: Date? = null
)
