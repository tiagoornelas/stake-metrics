package net.stakemetrics.persistence.jpa

import java.util.Optional
import java.util.UUID
import net.stakemetrics.persistence.models.RecoveryCodeModel
import org.springframework.data.jpa.repository.JpaRepository

interface RecoveryCodeJpaRepository : JpaRepository<RecoveryCodeModel, UUID> {
    fun findByCode(code: String): Optional<RecoveryCodeModel>
    fun findAllByUserId(userId: UUID): List<RecoveryCodeModel>
}