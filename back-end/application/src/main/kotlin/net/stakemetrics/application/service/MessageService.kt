package net.stakemetrics.application.service

import java.util.UUID
import net.stakemetrics.application.entities.Message
import net.stakemetrics.application.repositories.IMessageRepository
import org.springframework.stereotype.Service

@Service
class MessageService(private val messageRepository: IMessageRepository) {

    fun save(message: Message) {
        messageRepository.save(message)
    }

    fun setIntegrationMessageId(messageId: UUID, integrationMessageId: Int) {
        val message = messageRepository.findById(messageId)
        message.integrationMessageId = integrationMessageId
        messageRepository.save(message)
    }

}