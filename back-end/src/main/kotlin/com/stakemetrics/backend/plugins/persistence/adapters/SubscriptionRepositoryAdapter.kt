package com.stakemetrics.backend.plugins.persistence.adapters

import com.stakemetrics.backend.plugins.persistence.models.SubscriptionModel
import com.stakemetrics.backend.plugins.persistence.repositories.SubscriptionRepository
import com.stakemetrics.backend.plugins.subscription.entities.Subscription
import com.stakemetrics.backend.plugins.subscription.ports.SubscriptionRepositoryPort
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class SubscriptionRepositoryAdapter(private val subscriptionRepository: SubscriptionRepository) :
    SubscriptionRepositoryPort {
    override fun save(subscription: Subscription) {
        subscriptionRepository.save(subscription.toModel())
    }

    override fun findByUserId(userId: UUID): Subscription? {
        val queriedSubscriptions = subscriptionRepository.findByUserId(userId)
        return queriedSubscriptions?.toDomain()
    }

    override fun findByCustomerId(customerId: String): Subscription? {
        val queriedSubscriptions = subscriptionRepository.findByCustomerId(customerId)
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