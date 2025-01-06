package net.stakemetrics.persistence.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.entities.enums.MessengerChatStatus

@Entity
@Table(name = "messenger_chats")
data class MessengerChatModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    @ManyToOne @JoinColumn(name = "user_id")
    val user: UserModel? = null,
    val name: String? = null,
    val chatId: String? = null,
    val status: MessengerChatStatus = MessengerChatStatus.ACTIVE,
    val delay: Int = 0,
    val deliveryProbability: Double = 1.0,
    val notDeliveredMessage: String = "",
    val extraText: String = "",
    val receiveReports: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): MessengerChat {
        return MessengerChat(
            id,
            user?.toDomain(),
            name,
            chatId,
            status,
            delay,
            deliveryProbability,
            notDeliveredMessage,
            extraText,
            receiveReports,
            createdAt
        )
    }
}

fun MessengerChat.toModel(): MessengerChatModel {
    return MessengerChatModel(
        id,
        user?.toModel(),
        name,
        chatId,
        status,
        delay,
        deliveryProbability,
        notDeliveredMessage,
        extraText,
        receiveReports,
        createdAt
    )
}