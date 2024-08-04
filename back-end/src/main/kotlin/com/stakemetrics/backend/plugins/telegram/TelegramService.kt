package com.stakemetrics.backend.plugins.telegram

import com.stakemetrics.backend.domain.exceptions.AlreadyIntegratedException
import com.stakemetrics.backend.domain.exceptions.IntegrationException
import com.stakemetrics.backend.domain.exceptions.InvalidFieldException
import com.stakemetrics.backend.domain.exceptions.NotFoundException
import com.stakemetrics.backend.plugins.http.dto.TelegramDTO
import com.stakemetrics.backend.plugins.http.ports.TelegramServicePort
import com.stakemetrics.backend.plugins.http.ports.UserServicePort
import com.stakemetrics.backend.plugins.telegram.entities.ChatDetails
import com.stakemetrics.backend.plugins.telegram.entities.TelegramChat
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
        val telegramChat = TelegramChat(id = UUID.randomUUID(), user = user, chatId = channelId, name = "Meu Canal")
        telegramChatRepositoryPort.save(telegramChat)
    }

    override fun editIntegrationSettings(dto: TelegramDTO.EditIntegrationRequest) {
        if (dto.delay != null && dto.delay > 120)
            throw InvalidFieldException("Delay", dto.delay.toString())

        if (dto.deliveryProbability != null && (dto.deliveryProbability < 0 || dto.deliveryProbability > 1))
            throw InvalidFieldException("deliveryProbability", dto.deliveryProbability.toString())

        val queriedTelegramChat = telegramChatRepositoryPort.findById(dto.id) ?: throw NotFoundException(
            "TelegramChat",
            "id",
            dto.id.toString()
        )

        val editedTelegramChat = queriedTelegramChat.copy(
            name = dto.name ?: queriedTelegramChat.name,
            status = dto.status ?: queriedTelegramChat.status,
            chatId = dto.chatId ?: queriedTelegramChat.chatId,
            delay = dto.delay ?: queriedTelegramChat.delay,
            deliveryProbability = dto.deliveryProbability ?: queriedTelegramChat.deliveryProbability,
            notDeliveredMessage = dto.notDeliveredMessage,
            delayedAlertMessage = dto.delayedAlertMessage,
            extraText = dto.extraText
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
        // TODO - Implementar um sistema de fila para mensagens com agendamento
        val telegramChat = telegramChatRepositoryPort.findById(telegramChatId)
            ?: throw NotFoundException("TelegramChat", "id", telegramChatId.toString())

        if (telegramChat.chatId == null) throw NotFoundException(
            "TelegramChat chatId",
            "telegramChatId",
            telegramChatId.toString()
        )

        val delayInSeconds = telegramChat.delay.coerceAtMost(120)
        val hasDelayedMessage = telegramChat.hasDelayedAlertMessage()

        val deliveryProbability = telegramChat.deliveryProbability
        val hasNotDeliveredMessage = telegramChat.hasNotDeliveredMessage()

        runBlocking {
            launch {
                if (Random.nextDouble() <= deliveryProbability) {
                    if (hasDelayedMessage) {
                        telegramClient.execute(
                            SendMessage
                                .builder()
                                .chatId(telegramChat.chatId)
                                .text(telegramChat.delayedAlertMessage)
                                .build()
                        )
                    }
                    delay(delayInSeconds * 1000L)

                    val finalMessage = if (!telegramChat.extraText.isNullOrEmpty()) {
                        "$message\n\n${telegramChat.extraText}"
                    } else {
                        message
                    }

                    telegramClient.execute(
                        SendMessage
                            .builder()
                            .chatId(telegramChat.chatId)
                            .text(finalMessage)
                            .build()
                    )
                } else {
                    if (hasNotDeliveredMessage) {
                        telegramClient.execute(
                            SendMessage
                                .builder()
                                .chatId(telegramChat.chatId)
                                .text(telegramChat.notDeliveredMessage)
                                .build()
                        )
                    }
                }
            }
        }
    }

    fun getChatIdWithPassPhrase(passPhrase: UUID): ChatDetails {
        val query = GetUpdates.builder().build()
        val updates = telegramClient.execute(query)
        val chat = updates
            .filter { it.message != null }
            .find { it.message.text == passPhrase.toString() }?.message?.chat
            ?: throw IntegrationException("Telegram chat with passphrase not found")

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

