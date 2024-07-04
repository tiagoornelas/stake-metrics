package com.stakemetrics.backend.plugins.telegram.ports

import com.stakemetrics.backend.plugins.telegram.entities.TelegramChat
import java.util.UUID

interface TelegramChatRepositoryPort {
    fun save(telegramChat: TelegramChat)
    fun delete(telegramChat: TelegramChat)
    fun findById(id: UUID): TelegramChat?
    fun findByUserId(id: UUID): TelegramChat?
    fun findAllByUserId(id: UUID): List<TelegramChat>
    fun findAllByUserIdAndChatId(userId: UUID, chatId: String): List<TelegramChat>
}