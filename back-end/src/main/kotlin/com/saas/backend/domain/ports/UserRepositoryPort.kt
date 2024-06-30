package com.stakemetrics.backend.domain.ports

import com.stakemetrics.backend.domain.entities.User
import java.util.UUID

interface UserRepositoryPort {
    fun findById(userId: UUID): User?
    fun findByEmail(email: String): User?
    fun save(user: User): User
}