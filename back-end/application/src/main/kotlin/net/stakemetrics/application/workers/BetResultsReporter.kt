package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.service.IMessengerService
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class BetResultsReporter(
    private val fifaBetRepository: IFifaBetRepository,
    private val fifaBetMessageBuilder: FifaBetMessageBuilder,
    private val messengerService: IMessengerService
) {

    fun reportChat(messengerChat: MessengerChat) {
        val now = LocalDateTime.now()
        val startDate = now.minusDays(6).withStartOfDay()
        val endDate = now.withEndOfDay()

        val fifaBets = fifaBetRepository.findByMessengerChatAndDateBetween(
            messengerChat.id, startDate, endDate
        )

        val reportMessage = fifaBetMessageBuilder.buildReport(fifaBets)
        messengerService.sendToChat(messengerChat, reportMessage, null)
        println(reportMessage)
    }

    private fun LocalDateTime.withStartOfDay(): LocalDateTime {
        return this.withHour(0).withMinute(0).withSecond(0)
    }

    private fun LocalDateTime.withEndOfDay(): LocalDateTime {
        return this.withHour(23).withMinute(59).withSecond(59)
    }
}