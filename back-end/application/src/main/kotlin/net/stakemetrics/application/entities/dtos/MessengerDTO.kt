package net.stakemetrics.application.entities.dtos

import java.util.UUID
import net.stakemetrics.application.entities.ChatDetails
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.entities.enums.MessengerChatStatus

class MessengerDTO {
    data class MessengerChatResponse(
        val id: String,
        val name: String?,
        val chatId: String?,
        val status: String,
        val delay: Int,
        val deliveryProbability: Double,
        val notDeliveredMessage: String,
        val extraText: String
    )

    data class ListResponse(
        val messengerChats: List<MessengerChatResponse>,
        val success: Boolean = true
    )

    data class BeginIntegrationResponse(
        val messengerChatId: UUID,
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
        val status: MessengerChatStatus?,
        val delay: Int?,
        val deliveryProbability: Double?,
        val notDeliveredMessage: String,
        val extraText: String
    )

    data class EditIntegrationResponse(
        val success: Boolean = true
    )

    data class DeleteIntegrationResponse(
        val success: Boolean = true
    )

    data class EnqueueRequest(
        val messengerChat: MessengerChat,
        val message: String,
        val messageId: UUID?
    )

    data class EditMessageEnqueueRequest(
        val messengerChat: MessengerChat,
        val integrationMessageId: Int,
        val newText: String
    )

    data class ReportBetResultsRequest(
        val messengerChat: MessengerChat
    )
}

fun MessengerChat.toMessengerChatResponse(): MessengerDTO.MessengerChatResponse {
    return MessengerDTO.MessengerChatResponse(
        this.id.toString(),
        this.name,
        this.chatId,
        this.status.toString(),
        this.delay,
        this.deliveryProbability,
        this.notDeliveredMessage,
        this.extraText
    )
}

fun ChatDetails.toChatDetailsResponse(): MessengerDTO.ChatDetailsResponse {
    return MessengerDTO.ChatDetailsResponse(this.id, this.username)
}