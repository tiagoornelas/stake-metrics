package net.stakemetrics.application.entities.dtos

import net.stakemetrics.application.entities.AutoBettor
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.enums.AutoBettorIntegrationStatus
import net.stakemetrics.application.entities.enums.UserTypes
import java.util.Date
import java.util.UUID

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

    data class SaveAutoBettorRequest(
        val integrationId: String
    )

    data class AutoBettorResponse(
        val autoBettor: AutoBettorWithoutUser?
    )

    data class AutoBettorWithoutUser(
        val id: UUID,
        val integrationId: String? = null,
        val name: String? = null,
        val status: AutoBettorIntegrationStatus,
        val createdAt: Date
    )

    data class ChangeAutoBettorStatusRequest(
        val status: AutoBettorIntegrationStatus
    )

    data class SaveAutoBettorResponse(
        val success: Boolean = true
    )

    data class DeleteAutoBettorResponse(
        val success: Boolean = true
    )

    data class ChangeAutoBettorResponse(
        val success: Boolean = true
    )

}

fun User.toUserResponse(subscription: SubscriptionDTO.SubscriptionResponse): UserDTO.UserResponse {
    return UserDTO.UserResponse(
        this.id, this.email, this.name, this.type, subscription
    )
}

fun AutoBettor.toAutoBettorResponse(): UserDTO.AutoBettorResponse {
    val autoBettorWithoutUser = UserDTO.AutoBettorWithoutUser(this.id, this.integrationId, this.name, this.status, this.createdAt)
    return UserDTO.AutoBettorResponse(autoBettorWithoutUser)
}
