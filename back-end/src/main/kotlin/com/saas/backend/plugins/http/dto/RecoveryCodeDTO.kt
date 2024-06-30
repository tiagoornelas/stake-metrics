package com.stakemetrics.backend.plugins.http.dto

import com.stakemetrics.backend.domain.entities.RecoveryCode
import org.springframework.stereotype.Component

@Component
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