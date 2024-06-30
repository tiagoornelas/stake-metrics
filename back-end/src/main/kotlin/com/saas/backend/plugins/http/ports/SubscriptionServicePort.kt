package com.stakemetrics.backend.plugins.http.ports

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.plugins.http.dto.SubscriptionDTO
import com.stakemetrics.backend.plugins.subscription.entities.Subscription
import org.springframework.stereotype.Service

@Service
interface SubscriptionServicePort {
    fun findByUser(user: User): Subscription
    fun listUserFeatures(user: User): Map<String, Int>
    fun createCheckoutSession(priceId: String, userEmail: String): SubscriptionDTO.CreateSessionResponse
    fun createPortalSession(userEmail: String): SubscriptionDTO.CreateSessionResponse
    fun createPricingTableInfo(userEmail: String): SubscriptionDTO.PricingTableResponse
}