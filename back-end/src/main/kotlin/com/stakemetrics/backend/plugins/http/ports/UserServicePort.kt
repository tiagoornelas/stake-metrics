package com.stakemetrics.backend.plugins.http.ports

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.plugins.http.dto.UserDTO
import java.util.UUID
import org.springframework.stereotype.Service

@Service
interface UserServicePort {
    fun create(dto: UserDTO.CreateRequest)
    fun edit(authenticatedEmail: String, userId: UUID, dto: UserDTO.EditRequest)
    fun changePassword(userId: UUID, dto: UserDTO.ChangePasswordRequest)
    fun findById(userId: UUID): User
    fun findByEmail(email: String): User?
}