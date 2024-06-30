package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.plugins.persistence.models.UserModel
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<UserModel, UUID> {
    fun findByEmail(email: String): Optional<UserModel>
}