package com.stakemetrics.backend.plugins.telegram.entities

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.plugins.telegram.enums.TelegramChatStatus
import java.util.UUID

data class TelegramChat(
    val id: UUID = UUID.randomUUID(),
    val user: User? = null,
    val name: String? = null,
    val chatId: String? = null,
    val status: TelegramChatStatus = TelegramChatStatus.ACTIVE,
    val delay: Int = 0,
    val deliveryProbability: Double = 1.0,
    val notDeliveredMessage: String = "",
    val delayedAlertMessage: String = "",
    val extraText: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun hasNotDeliveredMessage(): Boolean {
        return notDeliveredMessage.isNotEmpty()
    }

    fun hasDelayedAlertMessage(): Boolean {
        return delayedAlertMessage.isNotEmpty()
    }

    fun hasExtraText(): Boolean {
        return extraText.isNotEmpty()
    }
}