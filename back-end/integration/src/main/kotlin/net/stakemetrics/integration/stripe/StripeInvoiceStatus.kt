package net.stakemetrics.integration.stripe

enum class StripeInvoiceStatus(val value: String) {
    DRAFT("draft"), OPEN("open"), PAID("paid"), VOID("void"), UNCOLLECTIBLE("uncollectible")
}