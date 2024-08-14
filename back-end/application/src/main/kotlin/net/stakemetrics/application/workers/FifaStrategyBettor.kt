package net.stakemetrics.application.workers

import org.springframework.stereotype.Component

@Component
class FifaStrategyBettor {
    fun bet() {
//        checkIfAlreadyBet()
//        saveBet()
//        if (!fifaBet.isPaperBet) notifyUser()
        println("Betting on Fifa Strategy")
    }

    private fun notifyUser() {
        println("Notifying user")
    }
}