package com.stakemetrics.backend.domain.ports

import com.stakemetrics.backend.domain.entities.RecoveryCode
import java.util.UUID
import org.springframework.stereotype.Repository

@Repository
interface RecoveryCodeRepositoryPort {
    fun save(recoveryCode: RecoveryCode)
    fun findByCode(code: String): RecoveryCode?
    fun findAllByUserId(userId: UUID): List<RecoveryCode>
    fun delete(recoveryCode: RecoveryCode)
}