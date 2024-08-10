package net.stakemetrics.http.security

import net.stakemetrics.application.entities.dtos.UserDTO
import net.stakemetrics.application.entities.exceptions.PasswordDoesNotMatchException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class CredentialsChecker(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val securityTokenService: SecurityTokenService
) {
    fun checkAndGenerateToken(credentials: UserDTO.LoginRequest): UserDTO.LoginResponse {
        val auth = checkCredentials(credentials)
        val userId = (auth.principal as CustomUserDetails).getUser().id
        return UserDTO.LoginResponse(generateToken(auth), userId)
    }

    private fun checkCredentials(credentials: UserDTO.LoginRequest): Authentication {
        try {
            return authenticationConfiguration.authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    credentials.email,
                    credentials.password
                )
            )
        } catch (e: BadCredentialsException) {
            throw PasswordDoesNotMatchException()
        }

    }

    private fun generateToken(auth: Authentication): String {
        return securityTokenService.generateToken((auth.principal as CustomUserDetails).getUser())
    }
}