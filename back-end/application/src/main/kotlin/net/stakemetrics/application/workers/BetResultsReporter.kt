package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.MessengerChat
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.service.IMessengerService
import java.time.LocalDateTime
import java.time.ZoneId
import org.springframework.stereotype.Service

@Service
class BetResultsReporter(
    private val messengerService: IMessengerService,
    private val fifaBetRepository: IFifaBetRepository,
    private val fifaBetMessageBuilder: FifaBetMessageBuilder
) {

    fun reportChat(messengerChat: MessengerChat) {
        val brazilZone = ZoneId.of("America/Sao_Paulo")
        val now = LocalDateTime.now(brazilZone)
        val targetDate = if (now.hour < 1) now.minusDays(1) else now
        
        val startDate = targetDate.withHour(0).withMinute(0).withSecond(0)
        val endDate = targetDate.withHour(23).withMinute(59).withSecond(59)

        val fifaBets = fifaBetRepository.findByMessengerChatAndDateBetween(
            messengerChat.id, startDate, endDate
        )

        val reportMessage = fifaBetMessageBuilder.buildReport(fifaBets, targetDate)
        messengerService.sendToQueue(messengerChat, reportMessage, null)
    }
}