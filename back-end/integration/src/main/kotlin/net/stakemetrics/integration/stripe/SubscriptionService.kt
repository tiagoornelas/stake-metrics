package net.stakemetrics.integration.stripe

import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.CustomerSession
import com.stripe.model.Invoice
import com.stripe.model.entitlements.ActiveEntitlement
import com.stripe.param.*
import com.stripe.param.entitlements.ActiveEntitlementListParams
import java.util.Date
import javax.annotation.PostConstruct
import net.stakemetrics.application.entities.Subscription
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.SubscriptionDTO
import net.stakemetrics.application.entities.enums.EntitlementTypes
import net.stakemetrics.application.entities.enums.SubscriptionStatus
import net.stakemetrics.application.repositories.ISubscriptionRepository
import net.stakemetrics.application.service.ISubscriptionService
import net.stakemetrics.application.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.stripe.model.Subscription as StripeSubscription
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

    override fun editSubscription(user: User) {
        val customer = Customer.retrieve(getIntegrationIdByUserEmail(user.email))
        val params = CustomerUpdateParams.builder().setName(user.name).setEmail(user.email).build()
        customer.update(params)
    }

    override fun getSubscriptionDetails(user: User): SubscriptionDTO.SubscriptionResponse {
        val subscription = subscriptionRepository.findByUser(user)
        val status = getStatus(subscription.integrationId)
        val expiresAt = getExpiresAt(subscription.integrationId)
        val features = listUserFeatures(subscription.integrationId)
        val hasPendingPayment = hasPendingPayment(subscription.integrationId)

        return SubscriptionDTO.SubscriptionResponse(
            subscription.id, subscription.integrationId, status, expiresAt, features, hasPendingPayment
        )
    }

    fun getStatus(integrationId: String): SubscriptionStatus {
        val subscriptions = getSubscriptions(integrationId)
        val isActive = subscriptions.any { it.status == SubscriptionStatus.ACTIVE.integrationValue }
        return if (isActive) SubscriptionStatus.ACTIVE else SubscriptionStatus.INACTIVE
    }

    fun getExpiresAt(integrationId: String): Date? {
        val subscriptions = getSubscriptions(integrationId)
        if (subscriptions.isEmpty()) return null

        val activeSubscriptions = subscriptions.filter { it.status == SubscriptionStatus.ACTIVE.integrationValue }
        val maxExpiresAt = activeSubscriptions.maxByOrNull { it.currentPeriodEnd }?.currentPeriodEnd
        if (maxExpiresAt != null) {
            return Date(maxExpiresAt * 1000)
        }

        val maxEndedAt = subscriptions.maxByOrNull { it.endedAt }?.endedAt
        return maxEndedAt?.let { Date(it * 1000) }
    }

    private fun getSubscriptions(integrationId: String): List<StripeSubscription> {
        val customer = Customer.retrieve(integrationId)
        val params = SubscriptionListParams.builder().setCustomer(customer.id).build()
        return StripeSubscription.list(params).data
    }

    private fun listUserFeatures(integrationId: String): Map<String, Int> {
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

    private fun hasPendingPayment(integrationId: String): Boolean {
        val subscriptions = getSubscriptions(integrationId)
        return subscriptions.any { subscription ->
            val invoices = Invoice.list(InvoiceListParams.builder().setSubscription(subscription.id).build()).data
            invoices.any { it.status == StripeInvoiceStatus.OPEN.value }
        }
    }

    private fun getIntegrationIdByUserEmail(userEmail: String): String {
        val user = userService.findByEmail(userEmail)
        return subscriptionRepository.findByUser(user).integrationId
    }

    override fun createCheckoutSession(
        priceId: String, userEmail: String
    ): SubscriptionDTO.CreateSessionResponse {
        val integrationId = getIntegrationIdByUserEmail(userEmail)

        return CheckoutSessionCreateParams.Builder()
            .setSuccessUrl(appBaseUrl)
            .setCancelUrl(appBaseUrl)
            .setCustomer(integrationId)
            .setCustomerEmail(userEmail)
            .setMode(CheckoutSessionCreateParams.Mode.SUBSCRIPTION)
            .setAllowPromotionCodes(true)
            .addLineItem(
                CheckoutSessionCreateParams.LineItem.Builder()
                    .setQuantity(1L)
                    .setPrice(priceId)
                    .build()
            )
            .build()
            .let { CheckoutSession.create(it) }
            .let { SubscriptionDTO.CreateSessionResponse(it.url) }
    }

    override fun createPortalSession(userEmail: String): SubscriptionDTO.CreateSessionResponse {
        val integrationId = getIntegrationIdByUserEmail(userEmail)

        return BillingPortalSessionCreateParams.Builder().setReturnUrl(appBaseUrl).setCustomer(integrationId).build()
            .let { BillingPortalSession.create(it) }.let { SubscriptionDTO.CreateSessionResponse(it.url) }
    }

    override fun createPricingTable(userEmail: String, darkMode: Boolean): SubscriptionDTO.PricingTableResponse {
        val integrationId = getIntegrationIdByUserEmail(userEmail)

        val params = CustomerSessionCreateParams.builder().setCustomer(integrationId).setComponents(
            CustomerSessionCreateParams.Components.builder().setPricingTable(
                CustomerSessionCreateParams.Components.PricingTable.builder().setEnabled(true).build()
            ).build()
        ).build()

        val pricingTableId = if (darkMode) stripeDarkModePricingTableId else stripePricingTableId
        val customerSessionClientSecret = CustomerSession.create(params)
        return SubscriptionDTO.PricingTableResponse(
            pricingTableId, stripePublicKey, customerSessionClientSecret.clientSecret
        )
    }

    override fun checkUserSubscriptionStatus(user: User): SubscriptionStatus {
        val integrationId = getIntegrationIdByUserEmail(user.email)
        val subscriptions = getSubscriptions(integrationId)
        val activeSubscriptions = subscriptions.filter { it.status == SubscriptionStatus.ACTIVE.integrationValue }
        return if (activeSubscriptions.isNotEmpty()) SubscriptionStatus.ACTIVE else SubscriptionStatus.INACTIVE
    }
}
