package com.stakemetrics.backend.plugins.subscription.enums

enum class EventTypes(val identifier: String) {
    CUSTOMER_SUBSCRIPTION_UPDATED("customer.subscription.updated"),
    CUSTOMER_SUBSCRIPTION_DELETED("customer.subscription.deleted"),
}