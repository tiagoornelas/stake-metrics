package net.stakemetrics.application.service

import net.stakemetrics.application.entities.Message
import net.stakemetrics.application.repositories.IMessageRepository
import org.springframework.stereotype.Service

@Service
class MessageService(private val messageRepository: IMessageRepository) {
    fun save(message: Message) {
        messageRepository.save(message)
    }
}