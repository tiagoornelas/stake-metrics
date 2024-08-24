package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.User

interface IUserRepository {
    fun save(user: User): User
    fun existsByEmail(email: String): Boolean
    fun findById(userId: UUID): User
    fun findByEmail(email: String): User
    fun findAll(): List<User>
}