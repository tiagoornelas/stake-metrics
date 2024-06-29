package com.saas.backend.domain.ports

import com.saas.backend.domain.entities.User
import org.springframework.stereotype.Service

@Service
interface SubscriptionServicePort {
    fun createCustomer(user: User)
}