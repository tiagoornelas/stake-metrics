package com.stakemetrics.backend.plugins.http.dto

import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.enums.UserTypes
import java.util.UUID
import org.springframework.stereotype.Component

class UserDTO {

    data class CreateRequest(
        val name: String, val email: String, val password: String, val passwordConfirmation: String
    )

    data class CreateResponse(
        val email: String, val success: Boolean = true
    )

    data class UserResponse(
        val id: UUID,
        val email: String,
        val name: String,
        val type: UserTypes,
        val subscription: SubscriptionDTO.SubscriptionResponse
    )

    data class FindResponse(
        val user: UserResponse, val success: Boolean = true
    )

    data class LoginRequest(
        val email: String, val password: String
    )

    data class LoginResponse(
        val token: String, val userId: UUID, val success: Boolean = true
    )

    data class EditRequest(
        val name: String, val email: String
    )

    data class EditResponse(
        val success: Boolean = true
    )

    data class ChangePasswordRequest(
        val currentPassword: String, val password: String, val passwordConfirmation: String
    )

    data class ChangePasswordResponse(
        val success: Boolean = true
    )
}

fun User.toUserResponse(subscription: SubscriptionDTO.SubscriptionResponse): UserDTO.UserResponse {
    return UserDTO.UserResponse(
        this.id, this.email, this.name, this.type, subscription
    )
}
