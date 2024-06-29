package com.saas.backend.plugins.subscription.ports

import com.saas.backend.plugins.subscription.entities.Subscription
import java.util.UUID

interface SubscriptionRepositoryPort {
    fun save(subscription: Subscription)
    fun findByUserId(userId: UUID): Subscription?
    fun findByCustomerId(customerId: String): Subscription?
}