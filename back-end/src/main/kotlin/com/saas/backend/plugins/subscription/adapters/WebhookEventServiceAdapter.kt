package com.saas.backend.plugins.subscription.adapters

import com.saas.backend.plugins.http.ports.WebhookEventsServicePort
import com.saas.backend.plugins.subscription.enums.EventTypes
import com.saas.backend.plugins.subscription.handlers.CustomerSubscriptionDeletedHandler
import com.saas.backend.plugins.subscription.handlers.CustomerSubscriptionUpdatedHandler
import com.saas.backend.plugins.subscription.handlers.EventHandler
import com.stripe.model.Event
import org.springframework.stereotype.Service

@Service
class WebhookEventServiceAdapter(
    private val customerSubscriptionUpdatedHandler: CustomerSubscriptionUpdatedHandler,
    private val customerSubscriptionDeletedHandler: CustomerSubscriptionDeletedHandler
) : WebhookEventsServicePort {

    override fun handle(event: Event) {
        val handler = getHandlerForEvent(event)
        handler?.handle(event)
    }

    private fun getHandlerForEvent(event: Event): EventHandler? {
        return when (event.type) {
            EventTypes.CUSTOMER_SUBSCRIPTION_UPDATED.identifier -> customerSubscriptionUpdatedHandler
            EventTypes.CUSTOMER_SUBSCRIPTION_DELETED.identifier -> customerSubscriptionDeletedHandler
            else -> null
        }
    }
}