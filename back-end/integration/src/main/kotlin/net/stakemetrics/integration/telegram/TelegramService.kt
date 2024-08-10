package net.stakemetrics.integration.telegram

import net.stakemetrics.application.entities.dtos.MessengerDTO
import net.stakemetrics.application.entities.exceptions.AlreadyIntegratedException
import net.stakemetrics.application.entities.exceptions.IntegrationException
import net.stakemetrics.application.entities.exceptions.InvalidFieldException
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.service.IMessengerService
import java.util.UUID
import kotlin.random.Random
import net.stakemetrics.application.entities.ChatDetails
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.repositories.IMessengerChatRepository
import net.stakemetrics.application.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates

@Service
class TelegramService(
    private val userService: UserService,
    private val messengerChatRepository: IMessengerChatRepository
) : IMessengerService {

    @Value("\${telegram.bot.token}")
    private val token: String = ""
    private val telegramClient by lazy { OkHttpTelegramClient(token) }

    override fun sendTestMessage(telegramChatId: UUID) {
        sendMessage(
            telegramChatId,
            "Esta é uma mensagem de teste do Stake Metrics. Isso signifia que sua integração com o Telegram está funcionando! ✅"
        )
    }

    override fun beginPrivateChatIntegration(userId: UUID): UUID {
        val user = userService.findById(userId)
        val telegramChat = MessengerChat(id = UUID.randomUUID(), user = user)
        messengerChatRepository.save(telegramChat)
        return telegramChat.id
    }

    override fun integratePrivateChat(userId: UUID): ChatDetails {
        val telegramChat = findWithoutIntegrationByUserWhileCleaningUp(userId)
        val chat = getChatIdWithPassPhrase(telegramChat.id)
        checkIfUserAlreadyHasIntegrationForThisChat(userId, chat.id)
        val telegramChatWithChatId = telegramChat.copy(chatId = chat.id, name = chat.username)
        messengerChatRepository.save(telegramChatWithChatId)
        return chat
    }

    private fun checkIfUserAlreadyHasIntegrationForThisChat(userId: UUID, chatId: String) {
        val chatsWithGivenChatIdForUser = messengerChatRepository.findAllByUserIdAndChatId(userId, chatId)
        if (chatsWithGivenChatIdForUser.isNotEmpty())
            throw AlreadyIntegratedException()
    }

    override fun integrateChannel(userId: UUID, channelId: String) {
        val user = userService.findById(userId)
        val telegramChat = MessengerChat(id = UUID.randomUUID(), user = user, chatId = channelId, name = "Meu Canal")
        messengerChatRepository.save(telegramChat)
    }

    override fun editIntegrationSettings(dto: MessengerDTO.EditIntegrationRequest) {
        if (dto.delay != null && dto.delay!! > 120)
            throw InvalidFieldException("Delay", dto.delay.toString())

        if (dto.deliveryProbability != null && (dto.deliveryProbability!! < 0 || dto.deliveryProbability!! > 1))
            throw InvalidFieldException("deliveryProbability", dto.deliveryProbability.toString())

        val queriedMessengerChat = messengerChatRepository.findById(dto.id)

        val editedMessengerChat = queriedMessengerChat.copy(
            name = dto.name ?: queriedMessengerChat.name,
            status = dto.status ?: queriedMessengerChat.status,
            chatId = dto.chatId ?: queriedMessengerChat.chatId,
            delay = dto.delay ?: queriedMessengerChat.delay,
            deliveryProbability = dto.deliveryProbability ?: queriedMessengerChat.deliveryProbability,
            notDeliveredMessage = dto.notDeliveredMessage,
            delayedAlertMessage = dto.delayedAlertMessage,
            extraText = dto.extraText
        )

        messengerChatRepository.save(editedMessengerChat)
    }

    override fun deleteIntegration(telegramChatId: UUID) {
        val queriedMessengerChat = messengerChatRepository.findById(telegramChatId)
        messengerChatRepository.delete(queriedMessengerChat)
    }

    override fun listIntegrationsForUser(userId: UUID): List<MessengerChat> {
        return messengerChatRepository.findAllByUserId(userId)
    }

    override fun sendMessage(telegramChatId: UUID, message: String) {
        // TODO - Implementar um sistema de fila para mensagens com agendamento
        val telegramChat = messengerChatRepository.findById(telegramChatId)

        if (telegramChat.chatId == null) throw NotFoundException(
            "MessengerChat chatId",
            "telegramChatId",
            telegramChatId.toString()
        )

        val delayInSeconds = telegramChat.delay.coerceAtMost(120)
        val hasDelayedMessage = telegramChat.hasDelayedAlertMessage()

        val deliveryProbability = telegramChat.deliveryProbability
        val hasNotDeliveredMessage = telegramChat.hasNotDeliveredMessage()

        if (Random.nextDouble() <= deliveryProbability) {
            if (hasDelayedMessage) {
                sendDelayedMessageAlert(telegramChat)
            }

            val finalMessage = getFinalMessage(telegramChat, message)

            sendTelegramMessage(telegramChat, finalMessage, delayInSeconds)
        } else {
            if (hasNotDeliveredMessage) {
                sendTelegramMessage(telegramChat, telegramChat.notDeliveredMessage)
            }
        }
    }

    private fun sendTelegramMessage(
        telegramChat: MessengerChat,
        finalMessage: String,
        delay: Int? = 0
    ) {
        telegramClient.execute(
            SendMessage
                .builder()
                .chatId(telegramChat.chatId!!)
                .text(finalMessage)
                .build()
        )
    }

    private fun getFinalMessage(
        telegramChat: MessengerChat,
        message: String
    ) = if (telegramChat.extraText.isNotEmpty()) {
        "$message\n\n${telegramChat.extraText}"
    } else {
        message
    }

    private fun sendDelayedMessageAlert(telegramChat: MessengerChat) {
        telegramClient.execute(
            SendMessage
                .builder()
                .chatId(telegramChat.chatId!!)
                .text(telegramChat.delayedAlertMessage)
                .build()
        )
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

    fun findWithoutIntegrationByUserWhileCleaningUp(id: UUID): MessengerChat {
        val telegramChats = messengerChatRepository.findAllByUserId(id)
        val telegramChatsWithoutIntegration = telegramChats.filter { it.chatId == null }

        if (telegramChatsWithoutIntegration.isEmpty()) {
            throw NotFoundException("MessengerChat", "user", id.toString())
        }

        val sortedChats = telegramChatsWithoutIntegration.sortedByDescending { it.createdAt }
        val remainingChat = sortedChats.first()
        val chatsToDelete = sortedChats.drop(1)

        chatsToDelete.forEach { messengerChatRepository.delete(it) }

        return remainingChat
    }
}

