package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.service.IMessengerService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BetResultsReporter(
    private val messengerService: IMessengerService,
    private val fifaBetRepository: IFifaBetRepository,
    private val fifaBetMessageBuilder: FifaBetMessageBuilder
) {

    fun reportChat(messengerChat: MessengerChat) {
        val user = messengerChat.user ?: throw IllegalStateException("MessengerChat must have an user")

        val targetDate = LocalDateTime.now().minusDays(1)
        val startDate = targetDate.withHour(0).withMinute(0).withSecond(0)
        val endDate = targetDate.withHour(23).withMinute(59).withSecond(59)

        val fifaBets = fifaBetRepository.findByMessengerChatAndDateBetween(
            messengerChat.id, user.timezoneOffset.id, startDate, endDate
        )

        if (fifaBets.isEmpty()) return

        val reportMessage = fifaBetMessageBuilder.buildReport(fifaBets, targetDate)
        messengerService.sendToQueue(messengerChat, reportMessage, null)
    }
}