package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IMessengerChatRepository
import net.stakemetrics.persistence.jpa.MessengerChatJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class MessengerChatRepository(private val messengerChatJpaRepository: MessengerChatJpaRepository) :
    IMessengerChatRepository {

    override fun save(messengerChat: MessengerChat) {
        messengerChatJpaRepository.save(messengerChat.toModel())
    }

    override fun delete(messengerChat: MessengerChat) {
        messengerChatJpaRepository.delete(messengerChat.toModel())
    }

    override fun findById(id: UUID): MessengerChat {
        val queriedMessengerChat = messengerChatJpaRepository.findById(id)
        return queriedMessengerChat.get().toDomain()
    }

    override fun findByUserId(userId: UUID): MessengerChat {
        val queriedMessengerChat = messengerChatJpaRepository.findByUserId(userId) ?: throw NotFoundException(
            "MessengerChat",
            "userId",
            userId.toString()
        )
        return queriedMessengerChat.toDomain()
    }

    override fun findAllByUserId(userId: UUID): List<MessengerChat> {
        val queriedMessengerChats = messengerChatJpaRepository.findAllByUserId(userId)
        return queriedMessengerChats.map { it.toDomain() }
    }

    override fun findAllByUserIdAndChatId(userId: UUID, chatId: String): List<MessengerChat> {
        val queriedMessengerChats = messengerChatJpaRepository.findAllByUserIdAndChatId(userId, chatId)
        return queriedMessengerChats.map { it.toDomain() }
    }
}
