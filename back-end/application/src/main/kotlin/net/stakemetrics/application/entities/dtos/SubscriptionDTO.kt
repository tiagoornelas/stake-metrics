package net.stakemetrics.application.entities.dtos

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.Subscription
import net.stakemetrics.application.entities.enums.SubscriptionStatus

class SubscriptionDTO {
    data class SubscriptionResponse(
        val id: UUID,
        val integrationId: String,
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

    data class SubscriptionEvent(
        val id: String,
        val type: String
    )

}

fun Subscription.toSubscriptionResponse(features: Map<String, Int> = emptyMap<String, Int>()): SubscriptionDTO.SubscriptionResponse {
    return SubscriptionDTO.SubscriptionResponse(
        this.id, this.integrationId, this.status, this.expiresAt, this.subscriptionId, features
    )
}