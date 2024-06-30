package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.plugins.persistence.jpa.SubscriptionJpaRepository
import com.stakemetrics.backend.plugins.persistence.models.SubscriptionModel
import com.stakemetrics.backend.plugins.subscription.entities.Subscription
import com.stakemetrics.backend.plugins.subscription.ports.SubscriptionRepositoryPort
import java.util.UUID
import org.springframework.stereotype.Repository

@Repository
class SubscriptionRepository(private val subscriptionJpaRepository: SubscriptionJpaRepository) :
    SubscriptionRepositoryPort {
    override fun save(subscription: Subscription) {
        subscriptionJpaRepository.save(subscription.toModel())
    }

    override fun findByUserId(userId: UUID): Subscription? {
        val queriedSubscriptions = subscriptionJpaRepository.findByUserId(userId)
        return queriedSubscriptions?.toDomain()
    }

    override fun findByCustomerId(customerId: String): Subscription? {
        val queriedSubscriptions = subscriptionJpaRepository.findByCustomerId(customerId)
        return queriedSubscriptions?.toDomain()
    }

}

fun Subscription.toModel(): SubscriptionModel {
    return SubscriptionModel(
        id,
        user?.toModel(),
        customerId,
        subscriptionId,
        status,
        expiresAt
    )
}