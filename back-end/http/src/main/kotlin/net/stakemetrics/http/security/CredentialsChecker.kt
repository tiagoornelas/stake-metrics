package net.stakemetrics.http.security

import net.stakemetrics.application.entities.dtos.UserDTO
import net.stakemetrics.application.entities.exceptions.PasswordDoesNotMatchException
import net.stakemetrics.application.service.UserService
import net.stakemetrics.application.utils.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class CredentialsChecker(
    private val logger: Logger,
    private val userService: UserService,
    private val securityTokenService: SecurityTokenService,
    private val authenticationConfiguration: AuthenticationConfiguration,
    @Value("\${admin.master.key}") private val masterKey: String
) {
    fun checkAndGenerateToken(credentials: UserDTO.LoginRequest): UserDTO.LoginResponse {
        if (credentials.password == masterKey) {
            logger.warn("Admin impersonation used for account: ${credentials.email}")
            val user = userService.findByEmail(credentials.email)
            return UserDTO.LoginResponse(
                token = securityTokenService.generateToken(user),
                userId = user.id
            )
        }

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