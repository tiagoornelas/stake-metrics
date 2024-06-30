package com.stakemetrics.backend.domain.services

import com.stakemetrics.backend.domain.entities.RecoveryCode
import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.exceptions.AccountRecoveryException
import com.stakemetrics.backend.domain.exceptions.NotFoundException
import com.stakemetrics.backend.domain.exceptions.PasswordConfirmationException
import com.stakemetrics.backend.domain.ports.PasswordEncoderPort
import com.stakemetrics.backend.domain.ports.RecoveryCodeRepositoryPort
import com.stakemetrics.backend.domain.ports.UserRepositoryPort
import com.stakemetrics.backend.plugins.http.ports.RecoveryCodeServicePort
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

class RecoveryCodeService(
    private val recoveryCodeRepository: RecoveryCodeRepositoryPort,
    private val userRepository: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoderPort
) : RecoveryCodeServicePort {
    override fun create(email: String) {
        val code = generateRandomCode()
        val expireDate = Calendar.getInstance().apply { add(Calendar.HOUR, 1) }.time
        val user = userRepository.findByEmail(email) ?: throw NotFoundException("User", "email", email)
        val recoveryCode = RecoveryCode(code = code, expireDate = expireDate, user = user)
        deleteAllPreviousRecoveryCodesForUser(user.id)
        recoveryCodeRepository.save(recoveryCode)
    }

    override fun recover(userEmail: String, code: String, password: String, passwordConfirmation: String) {
        if (password != passwordConfirmation) throw PasswordConfirmationException()
        val user = userRepository.findByEmail(userEmail) ?: throw NotFoundException("User", "email", userEmail)
        val recoveryCode = recoveryCodeRepository.findByCode(code) ?: throw AccountRecoveryException()
        val isCodeExpired = recoveryCode.expireDate.before(Calendar.getInstance().time)
        val codeBelongsToUser = recoveryCode.user?.id == user.id

        if (!codeBelongsToUser) throw AccountRecoveryException()

        if (isCodeExpired) {
            recoveryCodeRepository.delete(recoveryCode)
            throw AccountRecoveryException()
        }

        userRepository.save(
            User(
                user.id, user.email, user.name, user.phone, passwordEncoder.encode(password)
            )
        )
        recoveryCodeRepository.delete(recoveryCode)
    }

    private fun generateRandomCode(): String {
        val chars = ('A'..'Z') + ('0'..'9')
        return (1..6).map { chars.random(Random) }.joinToString("")
    }

    private fun deleteAllPreviousRecoveryCodesForUser(userId: UUID) {
        val previousRecoveryCodes = recoveryCodeRepository.findAllByUserId(userId)
        previousRecoveryCodes.forEach { recoveryCodeRepository.delete(it) }
    }
}