package net.stakemetrics.application.workers.enqueuers

import net.stakemetrics.application.entities.dtos.MessengerDTO
import net.stakemetrics.application.service.IQueueService
import net.stakemetrics.application.service.MessageService
import org.springframework.stereotype.Service

@Service
class MessengerChatReportEnqueuer(
    private val messageService: MessageService, private val queueService: IQueueService
) {

    fun enqueue() {
        val messengerChats = messageService.getProneToReportMessengerChats()
        messengerChats.forEach { queueService.enqueueBetResultReport(MessengerDTO.ReportBetResultsRequest(it)) }
    }

}