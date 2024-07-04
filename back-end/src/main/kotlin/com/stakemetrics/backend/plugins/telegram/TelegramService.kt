package com.stakemetrics.backend.plugins.telegram

import com.stakemetrics.backend.domain.exceptions.AlreadyIntegratedException
import com.stakemetrics.backend.domain.exceptions.InvalidFieldException
import com.stakemetrics.backend.domain.exceptions.NotFoundException
import com.stakemetrics.backend.plugins.http.ports.TelegramServicePort
import com.stakemetrics.backend.plugins.http.ports.UserServicePort
import com.stakemetrics.backend.plugins.telegram.entities.ChatDetails
import com.stakemetrics.backend.plugins.telegram.entities.TelegramChat
import com.stakemetrics.backend.plugins.telegram.enums.TelegramChatStatus
import com.stakemetrics.backend.plugins.telegram.ports.TelegramChatRepositoryPort
import java.util.UUID
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates

class TelegramService(
    token: String,
    private val telegramChatRepositoryPort: TelegramChatRepositoryPort,
    private val userServicePort: UserServicePort
) : TelegramServicePort {
    private val telegramClient = OkHttpTelegramClient(token)

    override fun sendTestMessage(telegramChatId: UUID) {
        sendMessage(
            telegramChatId,
            "Esta é uma mensagem de teste do Stake Metrics. Isso signifia que sua integração com o Telegram está funcionando! ✅"
        )
    }

    override fun beginPrivateChatIntegration(userId: UUID): UUID {
        val user = userServicePort.findById(userId)
        val telegramChat = TelegramChat(id = UUID.randomUUID(), user = user)
        telegramChatRepositoryPort.save(telegramChat)
        return telegramChat.id
    }

    override fun integratePrivateChat(userId: UUID): ChatDetails {
        val telegramChat = findWithoutIntegrationByUserWhileCleaningUp(userId)
        val chat = getChatIdWithPassPhrase(telegramChat.id)
        checkIfUserAlreadyHasIntegrationForThisChat(userId, chat.id)
        val telegramChatWithChatId = telegramChat.copy(chatId = chat.id, name = chat.username)
        telegramChatRepositoryPort.save(telegramChatWithChatId)
        return chat
    }

    private fun checkIfUserAlreadyHasIntegrationForThisChat(userId: UUID, chatId: String) {
        val chatsWithGivenChatIdForUser = telegramChatRepositoryPort.findAllByUserIdAndChatId(userId, chatId)
        if (chatsWithGivenChatIdForUser.isNotEmpty())
            throw AlreadyIntegratedException()
    }

    override fun integrateChannel(userId: UUID, channelId: String) {
        val user = userServicePort.findById(userId)
        val telegramChat = TelegramChat(id = UUID.randomUUID(), user = user, chatId = channelId)
        telegramChatRepositoryPort.save(telegramChat)
    }

    override fun editIntegrationSettings(
        id: UUID,
        name: String?,
        chatId: String?,
        status: TelegramChatStatus?,
        delay: Int?,
        deliveryProbability: Double?,
        extraText: String?
    ) {
        if (delay != null && delay > 120)
            throw InvalidFieldException("Delay", delay.toString())

        if (deliveryProbability != null && (deliveryProbability < 0 || deliveryProbability > 1))
            throw InvalidFieldException("deliveryProbability", deliveryProbability.toString())

        val queriedTelegramChat = telegramChatRepositoryPort.findById(id) ?: throw NotFoundException(
            "TelegramChat",
            "id",
            id.toString()
        )

        val editedTelegramChat = queriedTelegramChat.copy(
            name = name ?: queriedTelegramChat.name,
            status = status ?: queriedTelegramChat.status,
            chatId = chatId ?: queriedTelegramChat.chatId,
            delay = delay ?: queriedTelegramChat.delay,
            deliveryProbability = deliveryProbability ?: queriedTelegramChat.deliveryProbability,
            extraText = extraText ?: queriedTelegramChat.extraText
        )

        telegramChatRepositoryPort.save(editedTelegramChat)
    }

    override fun deleteIntegration(telegramChatId: UUID) {
        val queriedTelegramChat = telegramChatRepositoryPort.findById(telegramChatId) ?: throw NotFoundException(
            "TelegramChat",
            "id",
            telegramChatId.toString()
        )
        telegramChatRepositoryPort.delete(queriedTelegramChat)
    }

    override fun listIntegrationsForUser(userId: UUID): List<TelegramChat> {
        return telegramChatRepositoryPort.findAllByUserId(userId)
    }

    override fun sendMessage(telegramChatId: UUID, message: String) {
        val telegramChat = telegramChatRepositoryPort.findById(telegramChatId)
            ?: throw NotFoundException("TelegramChat", "id", telegramChatId.toString())

        if (telegramChat.chatId == null) throw NotFoundException(
            "TelegramChat chatId",
            "telegramChatId",
            telegramChatId.toString()
        )

        val delayInSeconds = telegramChat.delay.coerceAtMost(120)
        val deliveryProbability = telegramChat.deliveryProbability

        runBlocking {
            launch {
                delay(delayInSeconds * 1000L)

                if (Random.nextDouble() <= deliveryProbability) {
                    telegramClient.execute(
                        SendMessage
                            .builder()
                            .chatId(telegramChat.chatId)
                            .text(message)
                            .build()
                    )
                }
            }
        }
    }

    fun getChatIdWithPassPhrase(passPhrase: UUID): ChatDetails {
        val query = GetUpdates.builder().build()
        val updates = telegramClient.execute(query)
        val chat = updates.find { it.message.text == passPhrase.toString() }?.message?.chat
            ?: throw NotFoundException("Chat", "passPhrase", passPhrase.toString())

        return ChatDetails(chat.id.toString(), chat.userName)
    }

    fun findWithoutIntegrationByUserWhileCleaningUp(id: UUID): TelegramChat {
        val telegramChats = telegramChatRepositoryPort.findAllByUserId(id)
        val telegramChatsWithoutIntegration = telegramChats.filter { it.chatId == null }

        if (telegramChatsWithoutIntegration.isEmpty()) {
            throw NotFoundException("TelegramChat", "user", id.toString())
        }

        val sortedChats = telegramChatsWithoutIntegration.sortedByDescending { it.createdAt }
        val remainingChat = sortedChats.first()
        val chatsToDelete = sortedChats.drop(1)

        chatsToDelete.forEach { telegramChatRepositoryPort.delete(it) }

        return remainingChat
    }
}

