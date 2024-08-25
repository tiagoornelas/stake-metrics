package net.stakemetrics.application.entities

import java.util.Date
import java.util.UUID

data class Message(
    val id: UUID = UUID.randomUUID(),
    val messengerChat: MessengerChat? = null,
    val text: String? = null,
    val time: Date = Date(),
    var integrationMessageId: Int? = null
)