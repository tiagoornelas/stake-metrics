package com.stakemetrics.backend.plugins.subscription.handlers

import com.stripe.model.Event

interface EventHandler {
    fun handle(event: Event)
}