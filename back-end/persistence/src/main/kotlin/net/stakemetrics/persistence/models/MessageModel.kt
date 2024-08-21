package net.stakemetrics.persistence.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID
import net.stakemetrics.application.entities.Message

@Entity
@Table(name = "messages")
data class MessageModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "messenger_chat_id")
    val messengerChat: MessengerChatModel? = null,
    val text: String? = null
) {
    fun toDomain(): Message {
        return Message(
            id,
            messengerChat?.toDomain(),
            text
        )
    }
}

fun Message.toModel(): MessageModel {
    return MessageModel(
        id,
        messengerChat?.toModel(),
        text
    )
}
