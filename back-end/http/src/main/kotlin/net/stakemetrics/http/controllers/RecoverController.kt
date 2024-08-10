package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.RecoveryCodeDTO
import net.stakemetrics.application.service.RecoveryCodeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/recover")
class RecoverController(private val recoveryCodeService: RecoveryCodeService) {
    @PostMapping
    fun recoverPassword(@RequestBody request: RecoveryCodeDTO.CreateRequest): ResponseEntity<RecoveryCodeDTO.CreateResponse> {
        recoveryCodeService.create(request.email)
        return ResponseEntity.status(HttpStatus.OK).body(RecoveryCodeDTO.CreateResponse())
    }

    @PutMapping
    fun changePassword(
        @RequestBody request: RecoveryCodeDTO.RecoverRequest
    ): ResponseEntity<RecoveryCodeDTO.RecoverResponse> {
        recoveryCodeService.recover(request)
        return ResponseEntity.status(HttpStatus.OK).body(RecoveryCodeDTO.RecoverResponse())
    }
}