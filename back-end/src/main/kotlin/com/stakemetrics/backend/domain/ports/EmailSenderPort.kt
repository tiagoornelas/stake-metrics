package com.stakemetrics.backend.domain.ports

import org.springframework.stereotype.Service

@Service
interface EmailSenderPort {
    fun sendRecoveryCodeEmail(username: String, userEmail: String, code: String)
}