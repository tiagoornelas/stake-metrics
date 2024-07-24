package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.plugins.telegram.entities.TelegramChat
import com.stakemetrics.backend.plugins.telegram.enums.TelegramChatStatus
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "telegram_chats")
data class TelegramChatModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    @ManyToOne @JoinColumn(name = "user_id")
    val user: UserModel? = null,
    val name: String? = null,
    val chatId: String? = null,
    val status: TelegramChatStatus = TelegramChatStatus.ACTIVE,
    val delay: Int = 0,
    val deliveryProbability: Double = 1.0,
    val extraText: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): TelegramChat {
        return TelegramChat(
            id,
            user?.toDomain(),
            name,
            chatId,
            status,
            delay,
            deliveryProbability,
            extraText,
            createdAt
        )
    }
}
