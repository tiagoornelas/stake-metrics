package com.stakemetrics.backend.domain.ports

import com.stakemetrics.backend.domain.entities.User
import java.util.UUID
import org.springframework.stereotype.Repository

@Repository
interface UserRepositoryPort {
    fun existsByEmail(email: String): Boolean
    fun findById(userId: UUID): User?
    fun findByEmail(email: String): User?
    fun save(user: User): User
}