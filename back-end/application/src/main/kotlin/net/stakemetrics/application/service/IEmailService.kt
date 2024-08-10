package net.stakemetrics.application.service

import org.springframework.stereotype.Service

@Service
interface IEmailService {
    fun sendRecoveryCodeEmail(username: String, userEmail: String, code: String)
}