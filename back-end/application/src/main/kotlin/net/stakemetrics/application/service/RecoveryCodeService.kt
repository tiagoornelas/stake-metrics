package net.stakemetrics.application.service

import java.util.Calendar
import java.util.UUID
import kotlin.random.Random
import net.stakemetrics.application.entities.RecoveryCode
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.dtos.RecoveryCodeDTO
import net.stakemetrics.application.entities.exceptions.AccountRecoveryException
import net.stakemetrics.application.entities.exceptions.PasswordConfirmationException
import net.stakemetrics.application.repositories.IRecoveryCodeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RecoveryCodeService @Autowired constructor(
    private val userService: UserService,
    private val emailService: IEmailService,
    private val passwordEncoder: PasswordEncoder,
    private val recoveryCodeRepository: IRecoveryCodeRepository
) {
    fun create(email: String) {
        val code = generateRandomCode()
        val expireDate = Calendar.getInstance().apply { add(Calendar.HOUR, 1) }.time
        val user = userService.findByEmail(email)
        val recoveryCode = RecoveryCode(code = code, expireDate = expireDate, user = user)
        deleteAllPreviousRecoveryCodesForUser(user.id)
        recoveryCodeRepository.save(recoveryCode)
        emailService.sendRecoveryCodeEmail(user.name, email, code)
    }

    fun recover(dto: RecoveryCodeDTO.RecoverRequest) {
        if (dto.password != dto.passwordConfirmation) throw PasswordConfirmationException()

        val user = userService.findByEmail(dto.email)
        val recoveryCode = recoveryCodeRepository.findByCode(dto.code) ?: throw AccountRecoveryException()
        val isCodeExpired = recoveryCode.expireDate.before(Calendar.getInstance().time)
        val codeBelongsToUser = recoveryCode.user?.id == user.id

        if (!codeBelongsToUser) throw AccountRecoveryException()

        if (isCodeExpired) {
            recoveryCodeRepository.delete(recoveryCode)
            throw AccountRecoveryException()
        }

        userService.save(
            User(
                user.id, user.email, user.name, passwordEncoder.encode(dto.password),
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