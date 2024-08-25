package net.stakemetrics.application.service

import java.util.UUID
import net.stakemetrics.application.entities.ChatDetails
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.MessengerDTO

interface IMessengerService {
    fun sendToQueue(messengerChat: MessengerChat, message: String, messageId: UUID?): Boolean
    fun sendToChat(messengerChat: MessengerChat, message: String, messageId: UUID?)
    fun editMessage(dto: MessengerDTO.EditMessageEnqueueRequest)
    fun sendTestMessage(messengerChatId: UUID)
    fun listIntegrationsForUser(userId: UUID): List<MessengerChat>
    fun listActiveUserChats(user: User): List<MessengerChat>
    fun beginPrivateChatIntegration(userId: UUID): UUID
    fun integratePrivateChat(userId: UUID): ChatDetails
    fun integrateChannel(userId: UUID, channelId: String)
    fun deleteIntegration(telegramChatId: UUID)
    fun editIntegrationSettings(dto: MessengerDTO.EditIntegrationRequest)
}