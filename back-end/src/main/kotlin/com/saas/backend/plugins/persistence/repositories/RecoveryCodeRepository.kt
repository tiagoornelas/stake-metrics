package com.saas.backend.plugins.persistence.repositories

import com.saas.backend.plugins.persistence.models.RecoveryCodeModel
import java.util.Optional
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface RecoveryCodeRepository : JpaRepository<RecoveryCodeModel, UUID> {
    fun findByCode(code: String): Optional<RecoveryCodeModel>
    fun findAllByUserId(userId: UUID): List<RecoveryCodeModel>
}