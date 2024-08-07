package com.stakemetrics.backend.domain.entities

import com.stakemetrics.backend.domain.enums.UserTypes
import java.time.ZoneOffset
import java.util.UUID

data class User(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val name: String,
    val password: String,
    val type: UserTypes = UserTypes.USER,
    val timezoneOffset: ZoneOffset = ZoneOffset.of("-03:00")
)
