package net.stakemetrics.application.service

import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.SubscriptionDTO
import net.stakemetrics.application.entities.enums.SubscriptionStatus
import org.springframework.stereotype.Service

@Service
interface ISubscriptionService {
    fun createSubscription(user: User)
    fun editSubscription(user: User)
    fun getSubscriptionDetails(user: User): SubscriptionDTO.SubscriptionResponse
    fun createCheckoutSession(priceId: String, userEmail: String): SubscriptionDTO.CreateSessionResponse
    fun createPortalSession(userEmail: String): SubscriptionDTO.CreateSessionResponse
    fun createPricingTable(userEmail: String, darkMode: Boolean): SubscriptionDTO.PricingTableResponse
    fun checkUserSubscriptionStatus(user: User): SubscriptionStatus
}