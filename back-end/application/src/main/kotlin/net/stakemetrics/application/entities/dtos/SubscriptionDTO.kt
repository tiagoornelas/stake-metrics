package net.stakemetrics.application.entities.dtos

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.enums.SubscriptionStatus

class SubscriptionDTO {
    data class SubscriptionResponse(
        val id: UUID,
        val integrationId: String,
        val status: SubscriptionStatus,
        val expiresAt: Date?,
        val features: Map<String, Int>
    )

    data class PricingTableResponse(
        val pricingTableId: String, val publicKey: String, val customerSessionClientSecret: String
    )

    data class CreateCheckoutSessionRequest(
        val priceId: String
    )

    data class CreateSessionResponse(
        val url: String
    )


}
