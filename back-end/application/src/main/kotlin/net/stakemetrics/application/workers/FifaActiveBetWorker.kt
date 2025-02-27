package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.AutoBettor
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.Message
import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.service.FifaStrategyService
import net.stakemetrics.application.service.IMessengerService
import net.stakemetrics.application.service.IQueueService
import net.stakemetrics.application.service.MessageService
import net.stakemetrics.application.service.UserService
import net.stakemetrics.application.utils.Logger
import org.springframework.stereotype.Service

@Service
class FifaActiveBetWorker(
    private val logger: Logger,
    private val userService: UserService,
    private val queueService: IQueueService,
    private val messageService: MessageService,
    private val messengerService: IMessengerService,
    private val fifaStrategyService: FifaStrategyService,
    private val fifaBetMessageBuilder: FifaBetMessageBuilder,
) {

    fun workOnBet(fifaBet: FifaBet) {
        requireNotNull(fifaBet.strategy?.user) { "FifaBet strategy user is null" }

        val integratedActiveUserChats = getIntegratedActiveUserChats(fifaBet.strategy?.user!!)
        val autoBettor = userService.getUserAutoBettor(fifaBet.strategy.user)

        sendBetMessagesToUserChats(fifaBet, integratedActiveUserChats)
        autoBet(fifaBet, autoBettor)

        val shouldForcePaperBet = integratedActiveUserChats.isEmpty() && autoBettor == null
        if (shouldForcePaperBet) forcePaperBet(fifaBet)
    }

    private fun getIntegratedActiveUserChats(user: User): List<MessengerChat> {
        return messengerService.listActiveUserChats(user).filter { it.chatId != null }
    }

    private fun sendBetMessagesToUserChats(fifaBet: FifaBet, integratedActiveUserChats: List<MessengerChat>) {
        val betMessage = fifaBetMessageBuilder.build(fifaBet)
        val successfulChats = mutableSetOf<MessengerChat>()

        integratedActiveUserChats.forEach { chat ->
            val message = Message(messengerChat = chat, text = betMessage)
            val messageSent = messengerService.sendToQueue(chat, betMessage, message.id)
            if (messageSent) {
                successfulChats.add(chat)
                messageService.save(message)
                fifaBet.messages.add(message)
            }
        }
    }

    private fun autoBet(fifaBet: FifaBet, autoBettor: AutoBettor?) {
        if (autoBettor != null && autoBettor.isActive()) {
            queueService.enqueueAutoBetTask(FifaBetDTO.AutoBetRequest(fifaBet, autoBettor))
        }
    }

    private fun forcePaperBet(fifaBet: FifaBet) {
        fifaBet.isPaperBet = true
        logger.log("Forcing paper bet for strategy ${fifaBet.strategy?.name}, user ${fifaBet.strategy?.user?.name}")
        fifaStrategyService.forcePaperBetStatus(fifaBet.strategy!!)
    }
}