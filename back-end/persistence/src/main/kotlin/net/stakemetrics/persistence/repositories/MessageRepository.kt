package net.stakemetrics.persistence.repositories

import net.stakemetrics.application.repositories.IMessageRepository
import net.stakemetrics.persistence.jpa.MessageJpaRepository
import org.springframework.stereotype.Repository

@Repository
class MessageRepository(private val messageJpaRepository: MessageJpaRepository) : IMessageRepository {
}