package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.persistence.models.MessageModel
import org.springframework.data.jpa.repository.JpaRepository

interface MessageJpaRepository : JpaRepository<MessageModel, UUID> {
    fun deleteAllByMessengerChatId(messengerChatId: UUID)
}