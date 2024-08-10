package net.stakemetrics.integration.stripe.service

import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.CustomerSession
import com.stripe.model.entitlements.ActiveEntitlement
import com.stripe.param.CustomerCreateParams
import com.stripe.param.CustomerSessionCreateParams
import com.stripe.param.entitlements.ActiveEntitlementListParams
import javax.annotation.PostConstruct
import net.stakemetrics.application.entities.Subscription
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.SubscriptionDTO
import net.stakemetrics.application.entities.enums.EntitlementTypes
import net.stakemetrics.application.repositories.ISubscriptionRepository
import net.stakemetrics.application.service.ISubscriptionService
import net.stakemetrics.application.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.stripe.model.billingportal.Session as BillingPortalSession
import com.stripe.model.checkout.Session as CheckoutSession
import com.stripe.param.billingportal.SessionCreateParams as BillingPortalSessionCreateParams
import com.stripe.param.checkout.SessionCreateParams as CheckoutSessionCreateParams

@Service
class SubscriptionService(
    private val userService: UserService,
    private val subscriptionRepository: ISubscriptionRepository,
) : ISubscriptionService {

    @Value("\${stripe.api.key}")
    private final val stripeApiKey: String = ""

    @Value("\${app.frontend.base.url}")
    private final val appBaseUrl: String = ""

    @Value("\${stripe.public.key}")
    private final val stripePublicKey: String = ""

    @Value("\${stripe.pricing.table}")
    private final val stripePricingTableId: String = ""

    @Value("\${stripe.dark.mode.pricing.table}")
    private final val stripeDarkModePricingTableId: String = ""

    @PostConstruct
    fun init() {
        Stripe.apiKey = stripeApiKey
    }

    override fun createSubscription(user: User) {
        val params = CustomerCreateParams.builder().setName(user.name).setEmail(user.email)
            .setMetadata(mapOf("userId" to user.id.toString())).build()

        val customer = Customer.create(params)
        val subscription = Subscription(integrationId = customer.id, user = user)
        subscriptionRepository.save(subscription)
    }

    override fun updateSubscription(subscription: Subscription) {
        subscriptionRepository.save(subscription)
    }

    override fun findByUser(user: User): Subscription {
        return subscriptionRepository.findByUser(user)
    }

    override fun findByIntegrationId(integrationId: String): Subscription {
        return subscriptionRepository.findByIntegrationId(integrationId)
    }

    override fun listUserFeatures(user: User): Map<String, Int> {
        val integrationId = getIntegrationIdByUserEmail(user.email)
        val params = ActiveEntitlementListParams.builder().setCustomer(integrationId).build()
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

    private fun getIntegrationIdByUserEmail(userEmail: String): String {
        val user = userService.findByEmail(userEmail)
        return subscriptionRepository.findByUser(user).integrationId
    }

    override fun createCheckoutSession(
        priceId: String, userEmail: String
    ): SubscriptionDTO.CreateSessionResponse {
        val integrationId = getIntegrationIdByUserEmail(userEmail)

        return CheckoutSessionCreateParams.Builder().setSuccessUrl(appBaseUrl).setCancelUrl(appBaseUrl)
            .setCustomer(integrationId).setMode(CheckoutSessionCreateParams.Mode.SUBSCRIPTION)
            .setAllowPromotionCodes(true)
            .addLineItem(
                CheckoutSessionCreateParams.LineItem.Builder().setQuantity(1L).setPrice(priceId).build()
            ).build().let { CheckoutSession.create(it) }.let { SubscriptionDTO.CreateSessionResponse(it.url) }
    }

    override fun createPortalSession(userEmail: String): SubscriptionDTO.CreateSessionResponse {
        val integrationId = getIntegrationIdByUserEmail(userEmail)

        return BillingPortalSessionCreateParams.Builder().setReturnUrl(appBaseUrl).setCustomer(integrationId).build()
            .let { BillingPortalSession.create(it) }.let { SubscriptionDTO.CreateSessionResponse(it.url) }
    }

    override fun createPricingTable(userEmail: String, darkMode: Boolean): SubscriptionDTO.PricingTableResponse {
        val integrationId = getIntegrationIdByUserEmail(userEmail)

        val params = CustomerSessionCreateParams.builder()
            .setCustomer(integrationId)
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

        val pricingTableId = if (darkMode) stripeDarkModePricingTableId else stripePricingTableId
        val customerSessionClientSecret = CustomerSession.create(params)
        return SubscriptionDTO.PricingTableResponse(
            pricingTableId,
            stripePublicKey,
            customerSessionClientSecret.clientSecret
        )
    }
}
