package net.stakemetrics.application.entities

import java.util.UUID

data class Message(
    val id: UUID = UUID.randomUUID(),
    val messengerChat: MessengerChat? = null,
    val text: String? = null
)