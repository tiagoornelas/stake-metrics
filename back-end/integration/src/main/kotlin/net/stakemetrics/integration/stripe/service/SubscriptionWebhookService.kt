package net.stakemetrics.integration.stripe.service

import net.stakemetrics.application.entities.dtos.SubscriptionDTO
import net.stakemetrics.application.entities.enums.EventTypes
import net.stakemetrics.application.service.ISubscriptionWebhookService
import net.stakemetrics.integration.stripe.handlers.CustomerSubscriptionDeletedHandler
import net.stakemetrics.integration.stripe.handlers.CustomerSubscriptionUpdatedHandler
import net.stakemetrics.integration.stripe.handlers.EventHandler
import org.springframework.stereotype.Service

@Service
class SubscriptionWebhookService(
    private val customerSubscriptionUpdatedHandler: CustomerSubscriptionUpdatedHandler,
    private val customerSubscriptionDeletedHandler: CustomerSubscriptionDeletedHandler
) : ISubscriptionWebhookService {

    override fun handle(event: SubscriptionDTO.SubscriptionEvent) {
        val handler = getHandlerForEvent(event)
        handler?.handle(event)
    }

    private fun getHandlerForEvent(event: SubscriptionDTO.SubscriptionEvent): EventHandler? {
        return when (event.type) {
            EventTypes.CUSTOMER_SUBSCRIPTION_UPDATED.identifier -> customerSubscriptionUpdatedHandler
            EventTypes.CUSTOMER_SUBSCRIPTION_DELETED.identifier -> customerSubscriptionDeletedHandler
            else -> null
        }
    }
}