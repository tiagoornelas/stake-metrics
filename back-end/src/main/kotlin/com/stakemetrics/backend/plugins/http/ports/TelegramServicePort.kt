package com.stakemetrics.backend.plugins.http.ports

import com.stakemetrics.backend.plugins.http.dto.TelegramDTO
import com.stakemetrics.backend.plugins.telegram.entities.ChatDetails
import com.stakemetrics.backend.plugins.telegram.entities.TelegramChat
import java.util.UUID
import org.springframework.stereotype.Service

@Service
interface TelegramServicePort {
    fun listIntegrationsForUser(userId: UUID): List<TelegramChat>
    fun sendMessage(telegramChatId: UUID, message: String)
    fun sendTestMessage(telegramChatId: UUID)
    fun beginPrivateChatIntegration(userId: UUID): UUID
    fun integratePrivateChat(userId: UUID): ChatDetails
    fun integrateChannel(userId: UUID, channelId: String)
    fun deleteIntegration(telegramChatId: UUID)
    fun editIntegrationSettings(dto: TelegramDTO.EditIntegrationRequest)
}