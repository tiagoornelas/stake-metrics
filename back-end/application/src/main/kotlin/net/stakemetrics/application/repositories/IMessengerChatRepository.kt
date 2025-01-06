package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.entities.enums.MessengerChatStatus

interface IMessengerChatRepository {
    fun save(messengerChat: MessengerChat)
    fun delete(messengerChat: MessengerChat)
    fun findById(id: UUID): MessengerChat
    fun findByUserId(userId: UUID): MessengerChat
    fun findAllByUserId(userId: UUID): List<MessengerChat>
    fun findAllByUserIdAndChatId(userId: UUID, chatId: String): List<MessengerChat>
    fun findAllByUserIdAndStatus(userId: UUID, status: MessengerChatStatus): List<MessengerChat>
    fun findAllActive(): List<MessengerChat>
}