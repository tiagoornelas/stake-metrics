package net.stakemetrics.application.utils

import net.stakemetrics.application.entities.exceptions.FifaStrategyRuleBreakException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Logger {
    private val logger: Logger = LoggerFactory.getLogger(Logger::class.java)

    fun log(message: String) {
        logger.info(message)
    }

    fun warn(message: String) {
        logger.warn(message)
    }

    fun logError(e: Exception) {
        logger.error(e.message)
    }

    fun logFifaStrategyRuleBreak(e: FifaStrategyRuleBreakException) {
        logger.info("Fifa Strategy Rule Break. We're not betting! Reason: ${e.message}")
    }
}