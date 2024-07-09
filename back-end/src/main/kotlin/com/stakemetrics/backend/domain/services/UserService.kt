package com.stakemetrics.backend.domain.services

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.exceptions.AlreadyExistsException
import com.stakemetrics.backend.domain.exceptions.EntityDoesntBelongToUserException
import com.stakemetrics.backend.domain.exceptions.InvalidFieldException
import com.stakemetrics.backend.domain.exceptions.NotFoundException
import com.stakemetrics.backend.domain.exceptions.PasswordConfirmationException
import com.stakemetrics.backend.domain.ports.PasswordEncoderPort
import com.stakemetrics.backend.domain.ports.SubscriptionServicePort
import com.stakemetrics.backend.domain.ports.UserRepositoryPort
import com.stakemetrics.backend.plugins.http.ports.UserServicePort
import java.util.UUID
import org.springframework.security.authentication.BadCredentialsException

class UserService(
    private val userRepository: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoderPort,
    private val subscriptionServicePort: SubscriptionServicePort
) : UserServicePort {
    override fun create(name: String, email: String, password: String, passwordConfirmation: String) {
        val userExists = checkUserExistence(email)
        if (userExists) throw AlreadyExistsException("User", email)

        if (password != passwordConfirmation) throw PasswordConfirmationException()

        val user = User(
            email = email, name = name, password = passwordEncoder.encode(password)
        )

        userRepository.save(user)
        subscriptionServicePort.createCustomer(user)
    }

    private fun checkUserExistence(email: String): Boolean {
        return userRepository.findByEmail(email) != null
    }

    override fun edit(authenticatedEmail: String, userId: UUID, name: String, email: String) {
        val user = userRepository.findById(userId) ?: throw NotFoundException("User", "id", userId.toString())

        checkEmailExistenceIfDifferent(email, user)
        if (user.email != authenticatedEmail) throw EntityDoesntBelongToUserException()

        val updatedUser = user.copy(name = name, email = email)
        userRepository.save(updatedUser)
    }

    private fun checkEmailExistenceIfDifferent(email: String, user: User) {
        if (email != user.email) {
            val userExists = checkUserExistence(email)
            if (userExists) throw AlreadyExistsException("User", email)
        }
    }

    override fun changePassword(userId: UUID, currentPassword: String, password: String, passwordConfirmation: String) {
        val user = userRepository.findById(userId) ?: throw NotFoundException("User", "id", userId.toString())

        if (password != passwordConfirmation) throw PasswordConfirmationException()
        if (!passwordEncoder.matches(currentPassword, user.password)) throw BadCredentialsException("Invalid password")

        val updatedUser = user.copy(password = passwordEncoder.encode(password))
        userRepository.save(updatedUser)
    }

    override fun findById(userId: UUID): User {
        return userRepository.findById(userId) ?: throw NotFoundException("User", "id", userId.toString())
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}