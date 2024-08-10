package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.persistence.models.SubscriptionModel
import org.springframework.data.jpa.repository.JpaRepository


interface SubscriptionJpaRepository : JpaRepository<SubscriptionModel, UUID> {
    fun findByUserId(userId: UUID): SubscriptionModel?
    fun findByIntegrationId(integrationId: String): SubscriptionModel?
}