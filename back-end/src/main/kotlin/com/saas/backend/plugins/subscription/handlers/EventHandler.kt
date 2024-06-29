package com.saas.backend.plugins.subscription.handlers

import com.stripe.model.Event

interface EventHandler {
    fun handle(event: Event)
}