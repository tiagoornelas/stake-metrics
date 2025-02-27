package net.stakemetrics.application.entities

import java.util.UUID
import net.stakemetrics.application.entities.enums.MessengerChatStatus

data class MessengerChat(
    val id: UUID = UUID.randomUUID(),
    val user: User? = null,
    val name: String? = null,
    val chatId: String? = null,
    val status: MessengerChatStatus = MessengerChatStatus.ACTIVE,
    val delay: Int = 0,
    val deliveryProbability: Double = 1.0,
    val notDeliveredMessage: String = "",
    val extraText: String = "",
    val receiveReports: Boolean = true,
    val hideSoftwareLink: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun hasNonDeliveryMessage(): Boolean {
        return notDeliveredMessage.isNotEmpty()
    }
}