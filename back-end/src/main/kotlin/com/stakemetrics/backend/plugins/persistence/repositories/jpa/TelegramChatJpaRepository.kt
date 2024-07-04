package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.plugins.persistence.models.TelegramChatModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface TelegramChatJpaRepository : JpaRepository<TelegramChatModel, UUID>{
    fun findByUserId(userId: UUID): TelegramChatModel?
    fun findAllByUserId(userId: UUID): List<TelegramChatModel>
    fun findAllByUserIdAndChatId(userId: UUID, chatId: String): List<TelegramChatModel>
}