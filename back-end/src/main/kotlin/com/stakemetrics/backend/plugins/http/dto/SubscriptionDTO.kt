package com.stakemetrics.backend.plugins.http.dto

import com.stakemetrics.backend.plugins.subscription.entities.Subscription
import com.stakemetrics.backend.plugins.subscription.enums.SubscriptionStatus
import java.util.Date
import java.util.UUID

class SubscriptionDTO {
    data class SubscriptionResponse(
        val id: UUID,
        val customerId: String,
        val status: SubscriptionStatus,
        val expiresAt: Date?,
        val subscriptionId: String?,
        val features: Map<String, Int>
    )

    data class PricingTableResponse(
        val pricingTableId: String,
        val publicKey: String,
        val customerSessionClientSecret: String
    )

    data class CreateCheckoutSessionRequest(
        val priceId: String
    )

    data class CreateSessionResponse(
        val url: String
    )
}

fun Subscription.toSubscriptionResponse(features: Map<String, Int> = emptyMap<String, Int>()): SubscriptionDTO.SubscriptionResponse {
    return SubscriptionDTO.SubscriptionResponse(
        this.id, this.customerId, this.status, this.expiresAt, this.subscriptionId, features
    )
}