package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.ports.UserRepositoryPort
import com.stakemetrics.backend.plugins.persistence.jpa.UserJpaRepository
import com.stakemetrics.backend.plugins.persistence.models.UserModel
import java.util.UUID
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val userJpaRepository: UserJpaRepository
) : UserRepositoryPort {
    override fun findById(userId: UUID): User? {
        val queriedUser = userJpaRepository.findById(userId).takeIf { it.isPresent } ?: return null
        return queriedUser.get().toDomain()
    }

    override fun findByEmail(email: String): User? {
        val queriedUser = userJpaRepository.findByEmail(email).takeIf { it.isPresent } ?: return null
        return queriedUser.get().toDomain()
    }

    override fun save(user: User): User {
        val savedUser = userJpaRepository.save(user.toModel())
        return savedUser.toDomain()
    }
}

fun User.toModel(): UserModel {
    return UserModel(
        id = id,
        email = email,
        name = name,
        passwordHash = password,
        phone = phone,
    )
}
