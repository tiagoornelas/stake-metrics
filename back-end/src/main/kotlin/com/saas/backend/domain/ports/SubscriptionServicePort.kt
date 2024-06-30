package com.stakemetrics.backend.domain.ports

import com.stakemetrics.backend.domain.entities.User
import org.springframework.stereotype.Service

@Service
interface SubscriptionServicePort {
    fun createCustomer(user: User)
}