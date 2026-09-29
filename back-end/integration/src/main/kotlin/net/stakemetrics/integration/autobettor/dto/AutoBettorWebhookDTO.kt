package net.stakemetrics.integration.autobettor.dto

import java.util.UUID

data class AutoBettorWebhookPayload(
    val betId: UUID,
    val matchIntegrationId: Long?,
    val homePlayer: String?,
    val awayPlayer: String?,
    val league: String?,
    val market: String,
    val odds: Double,
    val integrationId: String?,
    val timestamp: String
)
