package com.saas.backend.plugins.subscription.entities

import com.saas.backend.domain.entities.User
import com.saas.backend.plugins.subscription.enums.SubscriptionStatus
import java.util.Date
import java.util.UUID

data class Subscription(
    val id: UUID = UUID.randomUUID(),
    val user: User? = null,
    val customerId: String = "",
    val subscriptionId: String? = null,
    val status: SubscriptionStatus = SubscriptionStatus.INACTIVE,
    val expiresAt: Date? = null
)
