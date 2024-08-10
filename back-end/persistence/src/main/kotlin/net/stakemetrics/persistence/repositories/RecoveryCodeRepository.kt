package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.RecoveryCode
import net.stakemetrics.application.repositories.IRecoveryCodeRepository
import net.stakemetrics.persistence.jpa.RecoveryCodeJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class RecoveryCodeRepository(private val recoveryCodeJpaRepository: RecoveryCodeJpaRepository) :
    IRecoveryCodeRepository {

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
