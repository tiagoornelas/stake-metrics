package net.stakemetrics.persistence.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.RecoveryCode

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

fun RecoveryCode.toModel(): RecoveryCodeModel {
    return RecoveryCodeModel(id, code, expireDate, user?.toModel())
}