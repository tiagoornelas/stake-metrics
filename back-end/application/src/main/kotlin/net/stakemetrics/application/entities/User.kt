package net.stakemetrics.application.entities

import java.time.ZoneOffset
import java.util.UUID
import net.stakemetrics.application.entities.enums.UserTypes

data class User(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val name: String,
    val password: String,
    val type: UserTypes = UserTypes.USER,
    val timezoneOffset: ZoneOffset = ZoneOffset.of("-03:00")
)
