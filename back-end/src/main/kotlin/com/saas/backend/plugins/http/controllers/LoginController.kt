package com.saas.backend.plugins.http.controllers

import com.saas.backend.plugins.http.dto.UserDTO
import com.saas.backend.plugins.http.security.CredentialsChecker
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class LoginController(val securityCredentialsChecker: CredentialsChecker) {
    @PostMapping
    fun login(@RequestBody credentials: UserDTO.LoginRequest): ResponseEntity<UserDTO.LoginResponse> {
        val response = securityCredentialsChecker.checkAndGenerateToken(credentials)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}