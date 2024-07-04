package com.stakemetrics.backend.plugins.http.ports

import com.stakemetrics.backend.domain.entities.User
import java.util.UUID
import org.springframework.stereotype.Service

@Service
interface UserServicePort {
    fun create(name: String, email: String, phone: String, password: String, passwordConfirmation: String)
    fun edit(authenticatedEmail: String, userId: UUID, name: String, email: String, phone: String)
    fun changePassword(userId: UUID, currentPassword: String, password: String, passwordConfirmation: String)
    fun findById(userId: UUID): User
    fun findByEmail(email: String): User?
}