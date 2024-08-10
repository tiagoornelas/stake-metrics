package net.stakemetrics.application.repositories

import net.stakemetrics.application.entities.Subscription
import net.stakemetrics.application.entities.User

interface ISubscriptionRepository {
    fun save(subscription: Subscription)
    fun findByUser(user: User): Subscription
    fun findByIntegrationId(integrationId: String): Subscription
}