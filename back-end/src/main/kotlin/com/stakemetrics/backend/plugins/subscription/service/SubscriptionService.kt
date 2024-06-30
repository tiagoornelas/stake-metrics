package com.stakemetrics.backend.plugins.subscription.service

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.exceptions.NotFoundException
import com.stakemetrics.backend.domain.ports.UserRepositoryPort
import com.stakemetrics.backend.plugins.http.dto.SubscriptionDTO
import com.stakemetrics.backend.plugins.subscription.entities.Subscription
import com.stakemetrics.backend.plugins.subscription.enums.EntitlementTypes
import com.stakemetrics.backend.plugins.subscription.ports.SubscriptionRepositoryPort
import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.CustomerSession
import com.stripe.model.entitlements.ActiveEntitlement
import com.stripe.param.CustomerCreateParams
import com.stripe.param.CustomerSessionCreateParams
import com.stripe.param.entitlements.ActiveEntitlementListParams
import com.stakemetrics.backend.domain.ports.SubscriptionServicePort as DomainSubscriptionServicePort
import com.stakemetrics.backend.plugins.http.ports.SubscriptionServicePort as HttpSubscriptionServicePort
import com.stripe.model.billingportal.Session as BillingPortalSession
import com.stripe.model.checkout.Session as CheckoutSession
import com.stripe.param.billingportal.SessionCreateParams as BillingPortalSessionCreateParams
import com.stripe.param.checkout.SessionCreateParams as CheckoutSessionCreateParams


class SubscriptionService(
    stripeApiKey: String,
    private val stripePricingTableId: String,
    private val stripePublicKey: String,
    private val appBaseUrl: String,
    private val subscriptionRepositoryPort: SubscriptionRepositoryPort,
    private val userRepositoryPort: UserRepositoryPort
) : HttpSubscriptionServicePort, DomainSubscriptionServicePort {

    init {
        Stripe.apiKey = stripeApiKey
    }

    override fun createCheckoutSession(
        priceId: String, userEmail: String
    ): SubscriptionDTO.CreateSessionResponse {
        val customerId = getCustomerIdByUserEmail(userEmail)

        return CheckoutSessionCreateParams.Builder().setSuccessUrl(appBaseUrl).setCancelUrl(appBaseUrl)
            .setCustomer(customerId).setMode(CheckoutSessionCreateParams.Mode.SUBSCRIPTION)
            .setAllowPromotionCodes(true)
            .addLineItem(
                CheckoutSessionCreateParams.LineItem.Builder().setQuantity(1L).setPrice(priceId).build()
            ).build().let { CheckoutSession.create(it) }.let { SubscriptionDTO.CreateSessionResponse(it.url) }
    }

    override fun createPortalSession(userEmail: String): SubscriptionDTO.CreateSessionResponse {
        val customerId = getCustomerIdByUserEmail(userEmail)

        return BillingPortalSessionCreateParams.Builder().setReturnUrl(appBaseUrl).setCustomer(customerId).build()
            .let { BillingPortalSession.create(it) }.let { SubscriptionDTO.CreateSessionResponse(it.url) }
    }

    override fun createPricingTableInfo(userEmail: String): SubscriptionDTO.PricingTableResponse {
        val customerId = getCustomerIdByUserEmail(userEmail)

        val params = CustomerSessionCreateParams.builder()
            .setCustomer(customerId)
            .setComponents(
                CustomerSessionCreateParams.Components.builder()
                    .setPricingTable(
                        CustomerSessionCreateParams.Components.PricingTable.builder()
                            .setEnabled(true)
                            .build()
                    )
                    .build()
            )
            .build()

        val customerSessionClientSecret = CustomerSession.create(params)
        return SubscriptionDTO.PricingTableResponse(
            stripePricingTableId,
            stripePublicKey,
            customerSessionClientSecret.clientSecret
        )
    }

    override fun findByUser(user: User): Subscription {
        return subscriptionRepositoryPort.findByUserId(user.id) ?: throw NotFoundException(
            "Subscription", "userId", user.id.toString()
        )
    }

    override fun listUserFeatures(user: User): Map<String, Int> {
        val customerId = getCustomerIdByUserEmail(user.email)
        val params = ActiveEntitlementListParams.builder().setCustomer(customerId).build()
        val stripeLookupKeys = ActiveEntitlement.list(params).data.map { it.lookupKey }

        val featuresMap = mutableMapOf<String, Int>()
        for (lookupKey in stripeLookupKeys) {
            val entitlementType = EntitlementTypes.entries.find { it.identifier == lookupKey }
            if (entitlementType != null) {
                val featureType = entitlementType.featureType
                val amount = entitlementType.amount
                featuresMap[featureType.identifier] = amount
            }
        }

        return featuresMap
    }

    override fun createCustomer(user: User) {
        val params = CustomerCreateParams.builder().setName(user.name).setEmail(user.email)
            .setMetadata(mapOf("userId" to user.id.toString())).build()

        val customer = Customer.create(params)
        val subscription = Subscription(customerId = customer.id, user = user)
        subscriptionRepositoryPort.save(subscription)
    }

    private fun getCustomerIdByUserEmail(userEmail: String): String {
        val userId =
            userRepositoryPort.findByEmail(userEmail)?.id ?: throw NotFoundException("User", "email", userEmail)
        return subscriptionRepositoryPort.findByUserId(userId)?.customerId ?: throw NotFoundException(
            "Subscription", "userId", userId.toString()
        )
    }
}
