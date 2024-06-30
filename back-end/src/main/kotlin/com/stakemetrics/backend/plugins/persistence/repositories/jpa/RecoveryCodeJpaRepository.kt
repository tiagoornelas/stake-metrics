package com.stakemetrics.backend.plugins.persistence.jpa

import com.stakemetrics.backend.plugins.persistence.models.RecoveryCodeModel
import java.util.Optional
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface RecoveryCodeJpaRepository : JpaRepository<RecoveryCodeModel, UUID> {
    fun findByCode(code: String): Optional<RecoveryCodeModel>
    fun findAllByUserId(userId: UUID): List<RecoveryCodeModel>
}