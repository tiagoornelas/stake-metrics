package net.stakemetrics.persistence.jpa

import java.util.Optional
import java.util.UUID
import net.stakemetrics.persistence.models.UserModel
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserModel, UUID> {
    fun findByEmail(email: String): Optional<UserModel>
    fun existsByEmail(email: String): Boolean
}