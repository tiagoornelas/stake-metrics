package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.RecoveryCode

interface IRecoveryCodeRepository {
    fun save(recoveryCode: RecoveryCode)
    fun findByCode(code: String): RecoveryCode?
    fun findAllByUserId(userId: UUID): List<RecoveryCode>
    fun delete(recoveryCode: RecoveryCode)
}