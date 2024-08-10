package net.stakemetrics.application.utils

import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import org.springframework.stereotype.Component

@Component
class Logger {
    fun log(message: String) {
        println(message)
    }

    fun logFifaStrategyRuleBreak(e: FifaStrategyRuleBreakException) {
        println("Fifa Strategy Rule Break. We're not betting! Reason: ${e.message}")
    }
}