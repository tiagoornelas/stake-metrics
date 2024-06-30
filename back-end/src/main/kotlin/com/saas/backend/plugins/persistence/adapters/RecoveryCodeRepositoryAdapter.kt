package com.stakemetrics.backend.plugins.persistence.adapters

import com.stakemetrics.backend.domain.entities.RecoveryCode
import com.stakemetrics.backend.domain.ports.RecoveryCodeRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.RecoveryCodeModel
import com.stakemetrics.backend.plugins.persistence.repositories.RecoveryCodeRepository
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class RecoveryCodeRepositoryAdapter(private val recoveryCodeRepository: RecoveryCodeRepository) :
    RecoveryCodeRepositoryPort {
    override fun save(recoveryCode: RecoveryCode) {
        recoveryCodeRepository.save(recoveryCode.toModel())
    }

    override fun findByCode(code: String): RecoveryCode? {
        val queriedCode = recoveryCodeRepository.findByCode(code).takeIf { it.isPresent } ?: return null
        return queriedCode.get().toDomain()
    }

    override fun findAllByUserId(userId: UUID): List<RecoveryCode> {
        val queriedCodes = recoveryCodeRepository.findAllByUserId(userId)
        return queriedCodes.map { it.toDomain() }
    }

    override fun delete(recoveryCode: RecoveryCode) {
        recoveryCodeRepository.delete(recoveryCode.toModel())
    }

}

fun RecoveryCode.toModel(): RecoveryCodeModel {
    return RecoveryCodeModel(id, code, expireDate, user?.toModel())
}