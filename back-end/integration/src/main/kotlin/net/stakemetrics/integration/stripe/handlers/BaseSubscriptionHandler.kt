package com.stakemetrics.backend.plugin_stripe.handlers

import net.stakemetrics.application.entities.enums.SubscriptionStatus
import com.stripe.model.Event
import java.util.Date
import net.stakemetrics.application.entities.dtos.SubscriptionDTO
import net.stakemetrics.integration.stripe.handlers.EventHandler
import net.stakemetrics.integration.stripe.service.SubscriptionService
import org.springframework.stereotype.Service
import com.stripe.model.Subscription as StripeSubscription

@Service
abstract class BaseSubscriptionHandler(
    private val subscriptionService: SubscriptionService
) : EventHandler {

    protected fun handleSubscription(event: SubscriptionDTO.SubscriptionEvent) {
        val subscription = retrieveSubscription(event.id)
        val status = getSubscriptionStatus(subscription.status)
        val expiresAt = Date(subscription.currentPeriodEnd * 1000L)

        val subscriptionOnDatabase = subscriptionService.findByIntegrationId(subscription.customer)
        val updatedSubscription = subscriptionOnDatabase.copy(
            subscriptionId = subscription.id,
            status = status,
            expiresAt = expiresAt
        )

        subscriptionService.updateSubscription(updatedSubscription)
    }

    private fun retrieveSubscription(eventId: String): StripeSubscription {
        val retrievedEvent = Event.retrieve(eventId)
        return retrievedEvent.data.`object` as StripeSubscription
    }

    private fun getSubscriptionStatus(status: String): SubscriptionStatus {
        return if (status == SubscriptionStatus.ACTIVE.integrationValue) SubscriptionStatus.ACTIVE else SubscriptionStatus.INACTIVE
    }

}