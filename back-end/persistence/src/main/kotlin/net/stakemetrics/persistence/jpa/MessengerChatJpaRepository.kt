package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.application.entities.enums.MessengerChatStatus
import net.stakemetrics.persistence.models.MessengerChatModel
import org.springframework.data.jpa.repository.JpaRepository

interface MessengerChatJpaRepository : JpaRepository<MessengerChatModel, UUID> {
    fun findByUserId(userId: UUID): MessengerChatModel?
    fun findAllByUserId(userId: UUID): List<MessengerChatModel>
    fun findAllByUserIdAndChatId(userId: UUID, chatId: String): List<MessengerChatModel>
    fun findAllByUserIdAndStatus(userId: UUID, status: MessengerChatStatus): List<MessengerChatModel>
    fun findAllByStatus(status: MessengerChatStatus): List<MessengerChatModel>
}