package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.plugins.persistence.models.TelegramChatModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.TelegramChatJpaRepository
import com.stakemetrics.backend.plugins.telegram.entities.TelegramChat
import com.stakemetrics.backend.plugins.telegram.ports.TelegramChatRepositoryPort
import java.util.UUID
import org.springframework.stereotype.Repository

@Repository
class TelegramChatRepository(private val telegramChatJpaRepository: TelegramChatJpaRepository) :
    TelegramChatRepositoryPort {
    override fun save(telegramChat: TelegramChat) {
        telegramChatJpaRepository.save(telegramChat.toModel())
    }

    override fun delete(telegramChat: TelegramChat) {
        telegramChatJpaRepository.delete(telegramChat.toModel())
    }

    override fun findById(id: UUID): TelegramChat? {
        val queriedTelegramChat = telegramChatJpaRepository.findById(id)
        return queriedTelegramChat.get().toDomain()
    }

    override fun findByUserId(id: UUID): TelegramChat? {
        val queriedTelegramChat = telegramChatJpaRepository.findByUserId(id) ?: return null
        return queriedTelegramChat.toDomain()
    }

    override fun findAllByUserId(id: UUID): List<TelegramChat> {
        val queriedTelegramChats = telegramChatJpaRepository.findAllByUserId(id)
        return queriedTelegramChats.map { it.toDomain() }
    }

    override fun findAllByUserIdAndChatId(userId: UUID, chatId: String): List<TelegramChat> {
        val queriedTelegramChats = telegramChatJpaRepository.findAllByUserIdAndChatId(userId, chatId)
        return queriedTelegramChats.map { it.toDomain() }
    }
}

fun TelegramChat.toModel(): TelegramChatModel {
    return TelegramChatModel(
        id,
        user?.toModel(),
        name,
        chatId,
        status,
        delay,
        deliveryProbability,
        notDeliveredMessage,
        delayedAlertMessage,
        extraText
    )
}