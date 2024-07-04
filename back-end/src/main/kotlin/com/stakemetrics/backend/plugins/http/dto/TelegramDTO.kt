package com.stakemetrics.backend.plugins.http.dto

import com.stakemetrics.backend.plugins.telegram.entities.ChatDetails
import com.stakemetrics.backend.plugins.telegram.entities.TelegramChat
import com.stakemetrics.backend.plugins.telegram.enums.TelegramChatStatus
import java.util.UUID

class TelegramDTO {
    data class TelegramChatResponse(
        val id: String,
        val name: String?,
        val chatId: String?,
        val status: String,
        val delay: Int,
        val deliveryProbability: Double,
        val extraText: String?
    )

    data class ListResponse(
        val telegramChats: List<TelegramChatResponse>,
        val success: Boolean = true
    )

    data class BeginIntegrationResponse(
        val telegramChatId: UUID,
        val success: Boolean = true
    )

    data class ChatDetailsResponse(
        val id: String,
        val username: String?
    )

    data class IntegrateResponse(
        val chatDetails: ChatDetailsResponse,
        val success: Boolean = true
    )

    data class SendTestMessageResponse(
        val success: Boolean = true
    )

    data class IntegrateChannelRequest(
        val userId: UUID,
        val channelId: String
    )

    data class IntegrateChannelResponse(
        val success: Boolean = true
    )

    data class EditIntegrationRequest(
        val id: UUID,
        val name: String?,
        val chatId: String?,
        val status: TelegramChatStatus?,
        val delay: Int?,
        val deliveryProbability: Double?,
        val extraText: String?
    )

    data class EditIntegrationResponse(
        val success: Boolean = true
    )

    data class DeleteIntegrationResponse(
        val success: Boolean = true
    )
}

fun TelegramChat.toTelegramChatResponse(): TelegramDTO.TelegramChatResponse {
    return TelegramDTO.TelegramChatResponse(
        this.id.toString(),
        this.name,
        this.chatId,
        this.status.toString(),
        this.delay,
        this.deliveryProbability,
        this.extraText
    )
}

fun ChatDetails.toChatDetailsResponse(): TelegramDTO.ChatDetailsResponse {
    return TelegramDTO.ChatDetailsResponse(this.id, this.username)
}