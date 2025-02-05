package net.stakemetrics.application.utils

import net.stakemetrics.application.repositories.IMessengerChatRepository
import net.stakemetrics.application.workers.BetResultsReporter
import org.springframework.stereotype.Component
import java.util.*

@Component
class KotlinScript(private val betResultsReporter: BetResultsReporter, private val messengerChatRepository: IMessengerChatRepository) {
    fun run() {
        // Empty method
        val chat = messengerChatRepository.findById(UUID.fromString("e7446964-7614-411c-8476-ef88eccbb8e3"))
        betResultsReporter.reportChat(chat)
    }
}