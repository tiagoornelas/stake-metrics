package com.saas.backend.domain.entities

import java.util.Date
import java.util.UUID

data class RecoveryCode(
    val id: UUID = UUID.randomUUID(),
    val code: String,
    val expireDate: Date,
    val user: User?
)