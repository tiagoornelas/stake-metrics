package com.stakemetrics.backend.plugins.persistence.adapters

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.ports.UserRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.UserModel
import com.stakemetrics.backend.plugins.persistence.repositories.UserRepository
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class UserRepositoryAdapter(
    private val userRepository: UserRepository
) : UserRepositoryPort {
    override fun findById(userId: UUID): User? {
        val queriedUser = userRepository.findById(userId).takeIf { it.isPresent } ?: return null
        return queriedUser.get().toDomain()
    }

    override fun findByEmail(email: String): User? {
        val queriedUser = userRepository.findByEmail(email).takeIf { it.isPresent } ?: return null
        return queriedUser.get().toDomain()
    }

    override fun save(user: User): User {
        val savedUser = userRepository.save(user.toModel())
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
