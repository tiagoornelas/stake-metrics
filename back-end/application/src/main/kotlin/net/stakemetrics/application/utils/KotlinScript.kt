package net.stakemetrics.application.utils

import net.stakemetrics.application.repositories.IMessengerChatRepository
import net.stakemetrics.application.workers.BetResultsReporter
import org.springframework.stereotype.Component
import java.util.*

@Component
class KotlinScript(private val betResultsReporter: BetResultsReporter, private val messengerChatRepository: IMessengerChatRepository) {
    fun run() {
        val messengerChat = messengerChatRepository.findByUserId(UUID.fromString("a3ce35e3-dc21-4b76-9b38-232a95a1d354"))
        betResultsReporter.reportChat(messengerChat)
    }
}