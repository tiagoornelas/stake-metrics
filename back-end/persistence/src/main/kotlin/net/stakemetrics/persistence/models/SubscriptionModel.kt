package net.stakemetrics.persistence.models

import jakarta.persistence.*
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
) {
    fun toDomain(): Subscription {
        return Subscription(
            id,
            user?.toDomain(),
            integrationId,
        )
    }
}

fun Subscription.toModel(): SubscriptionModel {
    return SubscriptionModel(
        id,
        user?.toModel(),
        integrationId,
    )
}