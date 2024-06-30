package com.stakemetrics.backend.plugins.http.ports

import com.stripe.model.Event

interface WebhookEventsServicePort {
    fun handle(event: Event)
}