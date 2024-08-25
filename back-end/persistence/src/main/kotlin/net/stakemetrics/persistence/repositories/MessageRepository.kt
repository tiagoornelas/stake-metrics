package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.Message
import net.stakemetrics.application.repositories.IMessageRepository
import net.stakemetrics.persistence.jpa.MessageJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class MessageRepository(private val messageJpaRepository: MessageJpaRepository) : IMessageRepository {
    override fun save(message: Message) {
        messageJpaRepository.save(message.toModel())
    }

    override fun findById(messageId: UUID): Message {
        return messageJpaRepository.findById(messageId).map { it.toDomain() }.orElseThrow()
    }
}