package com.stakemetrics.backend.plugins.logger

import com.stakemetrics.backend.domain.exceptions.FifaStrategyRuleBreakException
import com.stakemetrics.backend.domain.ports.LoggerPort
import org.springframework.stereotype.Component

@Component
class Logger : LoggerPort {
    override fun log(message: String) {
        println(message)
    }

    override fun logFifaStrategyRuleBreak(e: FifaStrategyRuleBreakException) {
        println("Fifa Strategy Rule Break. We're not betting! Reason: ${e.message}")
    }
}