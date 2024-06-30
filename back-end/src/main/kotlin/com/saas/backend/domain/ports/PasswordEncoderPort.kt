package com.stakemetrics.backend.domain.ports

import org.springframework.stereotype.Service

@Service
interface PasswordEncoderPort {
    fun encode(password: String): String
    fun matches(password: String, encodedPassword: String): Boolean
}