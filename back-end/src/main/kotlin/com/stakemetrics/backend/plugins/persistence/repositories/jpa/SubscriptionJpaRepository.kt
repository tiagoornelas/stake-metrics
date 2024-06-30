package com.stakemetrics.backend.plugins.persistence.jpa

import com.stakemetrics.backend.plugins.persistence.models.SubscriptionModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface SubscriptionJpaRepository : JpaRepository<SubscriptionModel, UUID> {
    fun findByUserId(userId: UUID): SubscriptionModel?
    fun findByCustomerId(customerId: String): SubscriptionModel?
}