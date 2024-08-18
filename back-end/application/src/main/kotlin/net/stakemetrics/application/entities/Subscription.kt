package net.stakemetrics.application.entities

import java.util.UUID

data class Subscription(
    val id: UUID = UUID.randomUUID(),
    val user: User? = null,
    val integrationId: String = "",
)
