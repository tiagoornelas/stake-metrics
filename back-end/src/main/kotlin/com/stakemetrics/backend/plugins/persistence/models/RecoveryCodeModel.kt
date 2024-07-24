package com.stakemetrics.backend.plugins.persistence.models

import com.stakemetrics.backend.domain.entities.RecoveryCode
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "recovery_codes")
data class RecoveryCodeModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val code: String = "",
    val expireDate: Date = Date(),
    @ManyToOne @JoinColumn(name = "user_id")
    val user: UserModel? = null
) {
    fun toDomain(): RecoveryCode {
        return RecoveryCode(id, code, expireDate, user?.toDomain())
    }
}