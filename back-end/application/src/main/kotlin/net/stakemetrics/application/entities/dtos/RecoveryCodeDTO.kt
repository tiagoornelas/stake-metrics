package net.stakemetrics.application.entities.dtos

import net.stakemetrics.application.entities.RecoveryCode

class RecoveryCodeDTO {
    data class CreateRequest(
        val email: String
    )

    data class RecoverRequest(
        val code: String,
        val email: String,
        val password: String,
        val passwordConfirmation: String
    )

    data class CreateResponse(
        val success: Boolean = true
    )

    data class RecoverResponse(
        val success: Boolean = true
    )
}

fun RecoveryCode.toRecoveryCodeResponse(): RecoveryCodeDTO.CreateResponse {
    return RecoveryCodeDTO.CreateResponse()
}