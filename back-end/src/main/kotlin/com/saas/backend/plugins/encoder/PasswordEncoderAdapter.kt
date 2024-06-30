package com.stakemetrics.backend.plugins.encoder

import com.stakemetrics.backend.domain.ports.PasswordEncoderPort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class PasswordEncoderAdapter(private val bCryptPasswordEncoder: BCryptPasswordEncoder) : PasswordEncoderPort {
    override fun encode(password: String): String {
        return bCryptPasswordEncoder.encode(password)
    }

    override fun matches(password: String, encodedPassword: String): Boolean {
        return bCryptPasswordEncoder.matches(password, encodedPassword)
    }
}