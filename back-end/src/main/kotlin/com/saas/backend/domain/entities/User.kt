package com.saas.backend.domain.entities

import java.util.UUID

data class User(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val name: String,
    val phone: String,
    var password: String,
)
