package com.saas.backend.plugins.http.security

import com.saas.backend.plugins.http.dto.UserDTO
import com.saas.backend.plugins.persistence.models.UserModel
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class CredentialsChecker(
    private val authenticationManager: AuthenticationManager,
    private val securityTokenService: SecurityTokenService
) {
    fun checkAndGenerateToken(credentials: UserDTO.LoginRequest): UserDTO.LoginResponse {
        val auth = checkCredentials(credentials)
        val userId = (auth.principal as UserModel).id
        return UserDTO.LoginResponse(generateToken(auth), userId)
    }

    private fun checkCredentials(credentials: UserDTO.LoginRequest): Authentication {
        return authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                credentials.email,
                credentials.password
            )
        )
    }

    private fun generateToken(auth: Authentication): String {
        return securityTokenService.generateToken(auth.principal as UserModel)
    }
}