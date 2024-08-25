package net.stakemetrics.application.service

import java.util.UUID
import java.util.regex.Pattern
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.UserDTO
import net.stakemetrics.application.entities.exceptions.AlreadyExistsException
import net.stakemetrics.application.entities.exceptions.EntityDoesntBelongToUserException
import net.stakemetrics.application.entities.exceptions.PasswordConfirmationException
import net.stakemetrics.application.entities.exceptions.PasswordDoesNotMatchException
import net.stakemetrics.application.entities.exceptions.InvalidFieldException
import net.stakemetrics.application.repositories.IUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    private val userRepository: IUserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Lazy private val subscriptionService: ISubscriptionService
) {

    private val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )

    fun save(user: User) {
        userRepository.save(user)
    }

    fun create(dto: UserDTO.CreateRequest) {
        validateCreateRequest(dto)

        val userExists = checkUserExistence(dto.email)
        if (userExists) throw AlreadyExistsException("User", dto.email)

        if (dto.password != dto.passwordConfirmation) throw PasswordConfirmationException()

        val user = User(
            email = dto.email, name = dto.name, password = passwordEncoder.encode(dto.password)
        )

        userRepository.save(user)
        subscriptionService.createSubscription(user)
    }

    private fun validateCreateRequest(dto: UserDTO.CreateRequest) {
        if (dto.name.length < 2) throw IllegalArgumentException("Name must be at least 2 characters long")
        if (dto.password.length < 8) throw IllegalArgumentException("Password must be at least 8 characters long")
        if (!emailPattern.matcher(dto.email).matches()) throw IllegalArgumentException("Email is not valid")
    }

    private fun checkUserExistence(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    fun edit(authenticatedEmail: String, userId: UUID, dto: UserDTO.EditRequest) {
        val user = userRepository.findById(userId)

        checkEmailExistenceIfDifferent(dto.email, user)
        if (user.email != authenticatedEmail) throw EntityDoesntBelongToUserException()

        val updatedUser = user.copy(name = dto.name, email = dto.email)
        userRepository.save(updatedUser)
    }

    private fun checkEmailExistenceIfDifferent(email: String, user: User) {
        if (email != user.email) {
            val userExists = checkUserExistence(email)
            if (userExists) throw AlreadyExistsException("User", email)
        }
    }

    fun changePassword(userId: UUID, dto: UserDTO.ChangePasswordRequest) {
        val user = userRepository.findById(userId)

        if (dto.password != dto.passwordConfirmation) throw PasswordConfirmationException()
        if (!passwordEncoder.matches(dto.currentPassword, user.password)) throw PasswordDoesNotMatchException()

        val updatedUser = user.copy(password = passwordEncoder.encode(dto.password))
        userRepository.save(updatedUser)
    }

    fun findById(userId: UUID): User {
        return userRepository.findById(userId)
    }

    fun findByEmail(email: String): User {
        return userRepository.findByEmail(email)
    }
}