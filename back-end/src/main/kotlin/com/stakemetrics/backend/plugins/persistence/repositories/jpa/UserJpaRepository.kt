package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.plugins.persistence.models.UserModel
import java.util.Optional
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserModel, UUID> {
    fun findByEmail(email: String): Optional<UserModel>
    fun existsByEmail(email: String): Boolean
}