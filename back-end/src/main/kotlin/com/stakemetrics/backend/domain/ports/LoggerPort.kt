package com.stakemetrics.backend.domain.ports

import com.stakemetrics.backend.domain.exceptions.FifaStrategyRuleBreakException

interface LoggerPort {
    fun log(message: String)
    fun logFifaStrategyRuleBreak(e: FifaStrategyRuleBreakException)
}