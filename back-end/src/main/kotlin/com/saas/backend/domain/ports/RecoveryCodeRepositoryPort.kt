package com.saas.backend.domain.ports

import com.saas.backend.domain.entities.RecoveryCode
import java.util.UUID

interface RecoveryCodeRepositoryPort {
    fun save(recoveryCode: RecoveryCode)
    fun findByCode(code: String): RecoveryCode?
    fun findAllByUserId(userId: UUID): List<RecoveryCode>
    fun delete(recoveryCode: RecoveryCode)
}