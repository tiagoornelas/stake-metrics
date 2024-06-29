package com.saas.backend.plugins.persistence.repositories

import com.saas.backend.plugins.persistence.models.SubscriptionModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface SubscriptionRepository : JpaRepository<SubscriptionModel, UUID> {
    fun findByUserId(userId: UUID): SubscriptionModel?
    fun findByCustomerId(customerId: String): SubscriptionModel?
}