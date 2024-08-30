package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.Message

interface IMessageRepository {
    fun save(message: Message)
    fun findById(messageId: UUID): Message
    fun deleteAllByMessengerChatId(messengerChatId: UUID)
}