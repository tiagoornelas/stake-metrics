package com.saas.backend.plugins.subscription.handlers

import com.saas.backend.domain.exceptions.NotFoundException
import com.saas.backend.plugins.subscription.enums.SubscriptionStatus
import com.saas.backend.plugins.subscription.ports.SubscriptionRepositoryPort
import com.stripe.model.Event
import java.util.Date
import org.springframework.stereotype.Service
import com.stripe.model.Subscription as StripeSubscription

@Service
abstract class BaseSubscriptionHandler(
    private val subscriptionRepositoryPort: SubscriptionRepositoryPort
) : EventHandler {

    protected fun handleSubscription(event: Event) {
        val subscription = retrieveSubscription(event)
        val status = getSubscriptionStatus(subscription.status)
        val expiresAt = Date(subscription.currentPeriodEnd * 1000L)

        val subscriptionOnDatabase =
            subscriptionRepositoryPort.findByCustomerId(subscription.customer) ?: throw NotFoundException(
                "Subscription", "customerId", subscription.customer
            )

        val updatedSubscription = subscriptionOnDatabase.copy(
            subscriptionId = subscription.id,
            status = status,
            expiresAt = expiresAt
        )

        subscriptionRepositoryPort.save(updatedSubscription)
    }

    private fun retrieveSubscription(event: Event): StripeSubscription {
        val retrievedEvent = Event.retrieve(event.id)
        return retrievedEvent.data.`object` as StripeSubscription
    }

    private fun getSubscriptionStatus(status: String): SubscriptionStatus {
        return if (status == SubscriptionStatus.ACTIVE.integrationValue) SubscriptionStatus.ACTIVE else SubscriptionStatus.INACTIVE
    }

}