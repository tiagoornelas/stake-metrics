package net.stakemetrics.persistence.models

import net.stakemetrics.application.entities.enums.SubscriptionStatus
import jakarta.persistence.*
import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.Subscription

@Entity
@Table(name = "subscriptions")
data class SubscriptionModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "user_id")
    val user: UserModel? = null,
    val integrationId: String = "",
    val subscriptionId: String? = null,
    val status: SubscriptionStatus = SubscriptionStatus.INACTIVE,
    val expiresAt: Date? = null,
) {
    fun toDomain(): Subscription {
        return Subscription(
            id,
            user?.toDomain(),
            integrationId,
            subscriptionId,
            status,
            expiresAt
        )
    }
}

fun Subscription.toModel(): SubscriptionModel {
    return SubscriptionModel(
        id,
        user?.toModel(),
        integrationId,
        subscriptionId,
        status,
        expiresAt
    )
}