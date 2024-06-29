package com.saas.backend.plugins.http.ports

import org.springframework.stereotype.Service

@Service
interface RecoveryCodeServicePort {
    fun create(email: String)
    fun recover(userEmail: String, code: String, password: String, passwordConfirmation: String)
}