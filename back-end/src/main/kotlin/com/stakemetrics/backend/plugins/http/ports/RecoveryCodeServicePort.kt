package com.stakemetrics.backend.plugins.http.ports

import com.stakemetrics.backend.plugins.http.dto.RecoveryCodeDTO
import org.springframework.stereotype.Service

@Service
interface RecoveryCodeServicePort {
    fun create(email: String)
    fun recover(dto: RecoveryCodeDTO.RecoverRequest)
}