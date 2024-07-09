package com.stakemetrics.backend.plugins.http.security

import com.stakemetrics.backend.domain.exceptions.NotFoundException
import com.stakemetrics.backend.plugins.http.ports.UserServicePort
import com.stakemetrics.backend.plugins.persistence.repositories.toModel
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SecurityMiddleware(
    private val securityTokenService: SecurityTokenService,
    private val userServicePort: UserServicePort
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!isTokenPresent(request)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = recoverToken(request)!!
        addCredentialsOnRequestFilters(token)
        filterChain.doFilter(request, response)
    }

    private fun isTokenPresent(request: HttpServletRequest): Boolean {
        val token = request.getHeader("Authorization")
        return !token.isNullOrBlank()
    }

    fun recoverToken(request: HttpServletRequest): String? {
        val token = request.getHeader("Authorization")
        return token?.replace("Bearer ", "")
    }

    fun addCredentialsOnRequestFilters(token: String) {
        val jwt = securityTokenService.validateToken(token)
        val user = userServicePort.findByEmail(jwt.subject) ?: throw NotFoundException("User", "email", jwt.subject)
        val authentication = UsernamePasswordAuthenticationToken(jwt.subject, user.id, user.toModel().authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }
}