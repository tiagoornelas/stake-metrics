package com.stakemetrics.backend.plugins.http.controllers

import com.stakemetrics.backend.plugins.http.dto.RecoveryCodeDTO
import com.stakemetrics.backend.plugins.http.ports.RecoveryCodeServicePort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/recover")
class RecoverController(private val recoveryCodeServicePort: RecoveryCodeServicePort) {
    @PostMapping
    fun recoverPassword(@RequestBody request: RecoveryCodeDTO.CreateRequest): ResponseEntity<RecoveryCodeDTO.CreateResponse> {
        recoveryCodeServicePort.create(request.email)
        return ResponseEntity.status(HttpStatus.OK).body(RecoveryCodeDTO.CreateResponse())
    }

    @PutMapping
    fun changePassword(
        @RequestBody request: RecoveryCodeDTO.RecoverRequest
    ): ResponseEntity<RecoveryCodeDTO.RecoverResponse> {
        recoveryCodeServicePort.recover(request)
        return ResponseEntity.status(HttpStatus.OK).body(RecoveryCodeDTO.RecoverResponse())
    }
}