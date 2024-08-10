package net.stakemetrics.integration.stripe.handlers

import net.stakemetrics.application.entities.dtos.SubscriptionDTO

interface EventHandler {
    fun handle(event: SubscriptionDTO.SubscriptionEvent)
}