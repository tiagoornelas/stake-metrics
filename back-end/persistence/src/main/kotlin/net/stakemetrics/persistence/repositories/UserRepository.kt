package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IUserRepository
import net.stakemetrics.persistence.jpa.UserJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val userJpaRepository: UserJpaRepository) : IUserRepository {

    override fun existsByEmail(email: String): Boolean {
        return userJpaRepository.existsByEmail(email)
    }

    override fun findById(userId: UUID): User {
        val queriedUser = userJpaRepository.findById(userId).takeIf { it.isPresent } ?: throw NotFoundException(
            "User",
            "userId",
            userId.toString()
        )
        return queriedUser.get().toDomain()
    }

    override fun findByEmail(email: String): User {
        val queriedUser = userJpaRepository.findByEmail(email).takeIf { it.isPresent } ?: throw NotFoundException(
            "User",
            "email",
            email
        )
        return queriedUser.get().toDomain()
    }

    override fun findAll(): List<User> {
        return userJpaRepository.findAll().map { it.toDomain() }
    }

    override fun save(user: User): User {
        val savedUser = userJpaRepository.save(user.toModel())
        return savedUser.toDomain()
    }
}
