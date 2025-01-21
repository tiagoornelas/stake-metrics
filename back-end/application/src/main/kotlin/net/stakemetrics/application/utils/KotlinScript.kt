package net.stakemetrics.application.utils

import net.stakemetrics.application.repositories.IMessengerChatRepository
import net.stakemetrics.application.workers.BetResultsReporter
import org.springframework.stereotype.Component
import java.util.*

@Component
class KotlinScript(private val betResultsReporter: BetResultsReporter, private val messengerChatRepository: IMessengerChatRepository) {
    fun run() {
        // Empty method
    }
}