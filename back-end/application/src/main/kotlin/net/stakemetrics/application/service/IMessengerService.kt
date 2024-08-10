package net.stakemetrics.application.service

import java.util.UUID
import net.stakemetrics.application.entities.ChatDetails
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.entities.dtos.MessengerDTO

interface IMessengerService {
    fun listIntegrationsForUser(userId: UUID): List<MessengerChat>
    fun sendMessage(telegramChatId: UUID, message: String)
    fun sendTestMessage(telegramChatId: UUID)
    fun beginPrivateChatIntegration(userId: UUID): UUID
    fun integratePrivateChat(userId: UUID): ChatDetails
    fun integrateChannel(userId: UUID, channelId: String)
    fun deleteIntegration(telegramChatId: UUID)
    fun editIntegrationSettings(dto: MessengerDTO.EditIntegrationRequest)
}