package net.stakemetrics.integration.stripe.handlers

import com.stakemetrics.backend.plugin_stripe.handlers.BaseSubscriptionHandler
import net.stakemetrics.application.entities.dtos.SubscriptionDTO
import net.stakemetrics.integration.stripe.service.SubscriptionService
import org.springframework.stereotype.Service

@Service
class CustomerSubscriptionUpdatedHandler(
    subscriptionService: SubscriptionService
) : BaseSubscriptionHandler(subscriptionService) {

    override fun handle(event: SubscriptionDTO.SubscriptionEvent) {
        handleSubscription(event)
    }

}