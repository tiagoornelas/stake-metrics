package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.SubscriptionDTO
import net.stakemetrics.application.service.ISubscriptionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/subscription")
class SubscriptionController(
    private val subscriptionService: ISubscriptionService
) {

    @GetMapping("/plan/pricing-table")
    fun getAllPlans(
        @RequestParam(name = "darkMode", defaultValue = "false") darkMode: Boolean
    ): ResponseEntity<SubscriptionDTO.PricingTableResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val response = subscriptionService.createPricingTable(userEmail, darkMode)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/create-checkout-session")
    fun createCheckoutSession(@RequestBody request: SubscriptionDTO.CreateCheckoutSessionRequest): ResponseEntity<SubscriptionDTO.CreateSessionResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val response = subscriptionService.createCheckoutSession(request.priceId, userEmail)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/create-portal-session")
    fun createPortalSession(): ResponseEntity<SubscriptionDTO.CreateSessionResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val response = subscriptionService.createPortalSession(userEmail)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

}