package net.stakemetrics.persistence.repositories

import net.stakemetrics.application.entities.Subscription
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.ISubscriptionRepository
import net.stakemetrics.persistence.jpa.SubscriptionJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class SubscriptionRepository(private val subscriptionJpaRepository: SubscriptionJpaRepository) :
    ISubscriptionRepository {

    override fun save(subscription: Subscription) {
        subscriptionJpaRepository.save(subscription.toModel())
    }

    override fun findByUser(user: User): Subscription {
        val queriedSubscription = subscriptionJpaRepository.findByUserId(user.id) ?: throw NotFoundException(
            "Subscription",
            "userId",
            user.id.toString()
        )
        return queriedSubscription.toDomain()
    }

    override fun findByIntegrationId(integrationId: String): Subscription {
        val queriedSubscription =
            subscriptionJpaRepository.findByIntegrationId(integrationId) ?: throw NotFoundException(
                "Subscription",
                "integrationId",
                integrationId
            )
        return queriedSubscription.toDomain()
    }

}
