package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.RecoveryCode
import com.stakemetrics.backend.domain.ports.RecoveryCodeRepositoryPort
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.RecoveryCodeJpaRepository
import com.stakemetrics.backend.plugins.persistence.models.RecoveryCodeModel
import java.util.UUID
import org.springframework.stereotype.Repository

@Repository
class RecoveryCodeRepository(private val recoveryCodeJpaRepository: RecoveryCodeJpaRepository) :
    RecoveryCodeRepositoryPort {
    override fun save(recoveryCode: RecoveryCode) {
        recoveryCodeJpaRepository.save(recoveryCode.toModel())
    }

    override fun findByCode(code: String): RecoveryCode? {
        val queriedCode = recoveryCodeJpaRepository.findByCode(code).takeIf { it.isPresent } ?: return null
        return queriedCode.get().toDomain()
    }

    override fun findAllByUserId(userId: UUID): List<RecoveryCode> {
        val queriedCodes = recoveryCodeJpaRepository.findAllByUserId(userId)
        return queriedCodes.map { it.toDomain() }
    }

    override fun delete(recoveryCode: RecoveryCode) {
        recoveryCodeJpaRepository.delete(recoveryCode.toModel())
    }

}

fun RecoveryCode.toModel(): RecoveryCodeModel {
    return RecoveryCodeModel(id, code, expireDate, user?.toModel())
}