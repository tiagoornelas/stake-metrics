package com.stakemetrics.backend.plugins.http.controllers

import com.stakemetrics.backend.plugins.http.dto.SubscriptionDTO
import com.stakemetrics.backend.plugins.http.ports.SubscriptionServicePort
import com.stakemetrics.backend.plugins.http.ports.WebhookEventsServicePort
import com.stripe.model.Event
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/subscription")
class SubscriptionController(
    private val subscriptionServicePort: SubscriptionServicePort,
    private val webhookEventsServicePort: WebhookEventsServicePort
) {

    @GetMapping("/plan/pricing-table")
    fun getAllPlans(): ResponseEntity<SubscriptionDTO.PricingTableResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val response = subscriptionServicePort.createPricingTableInfo(userEmail)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/notify-event")
    fun notifyEvent(@RequestBody event: Event): ResponseEntity<Unit> {
        webhookEventsServicePort.handle(event)
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @PostMapping("/create-checkout-session")
    fun createCheckoutSession(@RequestBody request: SubscriptionDTO.CreateCheckoutSessionRequest): ResponseEntity<SubscriptionDTO.CreateSessionResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val response = subscriptionServicePort.createCheckoutSession(request.priceId, userEmail)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/create-portal-session")
    fun createPortalSession(): ResponseEntity<SubscriptionDTO.CreateSessionResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val response = subscriptionServicePort.createPortalSession(userEmail)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

}