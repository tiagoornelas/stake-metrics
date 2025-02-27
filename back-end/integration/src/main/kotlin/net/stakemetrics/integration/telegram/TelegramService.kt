package net.stakemetrics.integration.telegram

import jakarta.transaction.Transactional
import net.stakemetrics.application.entities.ChatDetails
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.MessengerDTO
import net.stakemetrics.application.entities.enums.FeatureTypes
import net.stakemetrics.application.entities.enums.MessengerChatStatus
import net.stakemetrics.application.entities.exceptions.AlreadyIntegratedException
import net.stakemetrics.application.entities.exceptions.IntegrationException
import net.stakemetrics.application.entities.exceptions.InvalidFieldException
import net.stakemetrics.application.entities.exceptions.NotAllowedException
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IMessageRepository
import net.stakemetrics.application.repositories.IMessengerChatRepository
import net.stakemetrics.application.service.IMessengerService
import net.stakemetrics.application.service.IQueueService
import net.stakemetrics.application.service.ISubscriptionService
import net.stakemetrics.application.service.MessageService
import net.stakemetrics.application.service.UserService
import net.stakemetrics.application.utils.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import java.util.UUID
import kotlin.random.Random

@Service
class TelegramService(
    private val logger: Logger,
    private val userService: UserService,
    private val queueService: IQueueService,
    private val messageService: MessageService,
    private val messageRepository: IMessageRepository,
    private val subscriptionService: ISubscriptionService,
    private val messengerChatRepository: IMessengerChatRepository
) : IMessengerService {

    @Value("\${telegram.bot.token}")
    private val token: String = ""
    private val telegramClient by lazy { OkHttpTelegramClient(token) }

    override fun sendToQueue(messengerChat: MessengerChat, message: String, messageId: UUID?): Boolean {
        try {
            val delayInSeconds = messengerChat.delay.coerceAtMost(120)

            val deliveryProbability = messengerChat.deliveryProbability
            val hasNotDeliveredMessage = messengerChat.hasNonDeliveryMessage()

            return if (Random.nextDouble() <= deliveryProbability) {
                val finalMessage = getFinalMessage(messengerChat, message)
                val payload = MessengerDTO.EnqueueRequest(messengerChat, finalMessage, messageId)
                queueService.enqueueMessageTask(payload, delayInSeconds)
                true
            } else {
                if (hasNotDeliveredMessage) {
                    val payload = MessengerDTO.EnqueueRequest(messengerChat, messengerChat.notDeliveredMessage, null)
                    queueService.enqueueMessageTask(payload, null)
                }
                false
            }
        } catch (e: Exception) {
            logger.logError(e)
            return false
        }
    }

    override fun sendToChat(messengerChat: MessengerChat, message: String, messageId: UUID?) {
        val telegramMessage = telegramClient.execute(
            SendMessage.builder().chatId(messengerChat.chatId!!).text(message).disableWebPagePreview(true).build()
        )

        if (messageId != null) messageService.setIntegrationMessageId(messageId, telegramMessage.messageId)
    }

    override fun editMessage(dto: MessengerDTO.EditMessageEnqueueRequest) {
        try {
            val editMessage = EditMessageText.builder()
                .chatId(dto.messengerChat.chatId!!)
                .messageId(dto.integrationMessageId)
                .text(dto.newText)
                .build()

            telegramClient.execute(editMessage)
        } catch (e: Exception) {
            logger.logError(e)
        }
    }

    override fun sendTestMessage(messengerChatId: UUID) {
        val messengerChat = messengerChatRepository.findById(messengerChatId)
        val testMessage = "Esta é uma mensagem de teste do Stake Metrics. Seu chat está funcionando! ✅"
        sendToQueue(messengerChat, testMessage, null)
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
        if (chatsWithGivenChatIdForUser.isNotEmpty()) throw AlreadyIntegratedException()
    }

    override fun integrateChannel(userId: UUID, channelId: String) {
        val user = userService.findById(userId)
        val telegramChat = MessengerChat(id = UUID.randomUUID(), user = user, chatId = channelId, name = "Meu Canal")
        messengerChatRepository.save(telegramChat)
    }

    override fun editIntegrationSettings(dto: MessengerDTO.EditIntegrationRequest) {
        if (dto.delay != null && dto.delay!! > 120) throw InvalidFieldException("Delay", dto.delay.toString())

        if (dto.deliveryProbability != null && (dto.deliveryProbability!! < 0 || dto.deliveryProbability!! > 1)) throw InvalidFieldException(
            "deliveryProbability",
            dto.deliveryProbability.toString()
        )

        val queriedMessengerChat = messengerChatRepository.findById(dto.id)

        if (dto.hideSoftwareLink == true) {
            val user = queriedMessengerChat.user ?: throw InvalidFieldException("user", "null")
            if (!subscriptionService.hasFeature(user, FeatureTypes.NO_ADS_MESSAGES)) {
                throw NotAllowedException("User does not have the NO_ADS_MESSAGES feature")
            }
        }

        val editedMessengerChat = queriedMessengerChat.copy(
            name = dto.name ?: queriedMessengerChat.name,
            status = dto.status ?: queriedMessengerChat.status,
            chatId = dto.chatId ?: queriedMessengerChat.chatId,
            delay = dto.delay ?: queriedMessengerChat.delay,
            deliveryProbability = dto.deliveryProbability ?: queriedMessengerChat.deliveryProbability,
            notDeliveredMessage = dto.notDeliveredMessage,
            extraText = dto.extraText,
            hideSoftwareLink = dto.hideSoftwareLink ?: queriedMessengerChat.hideSoftwareLink,
            receiveReports = dto.receiveReports ?: queriedMessengerChat.receiveReports
        )

        messengerChatRepository.save(editedMessengerChat)
    }

    @Transactional
    override fun deleteIntegration(telegramChatId: UUID) {
        val queriedMessengerChat = messengerChatRepository.findById(telegramChatId)
        messageRepository.deleteAllByMessengerChatId(telegramChatId)
        messengerChatRepository.delete(queriedMessengerChat)
    }

    override fun listIntegrationsForUser(userId: UUID): List<MessengerChat> {
        return messengerChatRepository.findAllByUserId(userId)
    }

    override fun listActiveUserChats(user: User): List<MessengerChat> {
        return messengerChatRepository.findAllByUserIdAndStatus(user.id, MessengerChatStatus.ACTIVE)
    }

    private fun getFinalMessage(
        messengerChat: MessengerChat, message: String
    ): String {
        val softwareLink = if (!messengerChat.hideSoftwareLink) {
            "\n\n🚀 Acesse https://stakemetrics.net para criar o seu robô"
        } else {
            ""
        }

        return if (messengerChat.extraText.isNotEmpty()) {
            "$message\n\n${messengerChat.extraText}$softwareLink"
        } else {
            "$message$softwareLink"
        }
    }

    fun getChatIdWithPassPhrase(passPhrase: UUID): ChatDetails {
        val query = GetUpdates.builder().build()
        val updates = telegramClient.execute(query)
        val chat =
            updates.filter { it.message != null }.find { it.message.text == passPhrase.toString() }?.message?.chat
                ?: throw IntegrationException("Telegram chat with passphrase not found")

        return ChatDetails(chat.id.toString(), chat.userName)
    }

    fun findWithoutIntegrationByUserWhileCleaningUp(id: UUID): MessengerChat {
        val messengerChats = messengerChatRepository.findAllByUserId(id)
        val messengerChatsWithoutIntegration = messengerChats.filter { it.chatId == null }

        if (messengerChatsWithoutIntegration.isEmpty()) {
            throw NotFoundException("MessengerChat", "user", id.toString())
        }

        val sortedChats = messengerChatsWithoutIntegration.sortedByDescending { it.createdAt }
        val remainingChat = sortedChats.first()
        val chatsToDelete = sortedChats.drop(1)

        chatsToDelete.forEach { messengerChatRepository.delete(it) }

        return remainingChat
    }
}
