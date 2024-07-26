package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.plugins.subscription.entities.Subscription
import com.stakemetrics.backend.plugins.subscription.enums.SubscriptionStatus
import jakarta.persistence.*
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "subscriptions")
data class SubscriptionModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    val user: UserModel? = null,
    val customerId: String = "",
    val subscriptionId: String? = null,
    val status: SubscriptionStatus = SubscriptionStatus.INACTIVE,
    val expiresAt: Date? = null,
) {
    fun toDomain(): Subscription {
        return Subscription(
            id,
            user?.toDomain(),
            customerId,
            subscriptionId,
            status,
            expiresAt
        )
    }
}