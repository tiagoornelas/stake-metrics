package com.saas.backend.plugins.subscription.handlers

import com.saas.backend.plugins.subscription.ports.SubscriptionRepositoryPort
import com.stripe.model.Event
import org.springframework.stereotype.Service

@Service
class CustomerSubscriptionUpdatedHandler(
    subscriptionRepositoryPort: SubscriptionRepositoryPort
) : BaseSubscriptionHandler(subscriptionRepositoryPort) {

    override fun handle(event: Event) {
        handleSubscription(event)
    }

}