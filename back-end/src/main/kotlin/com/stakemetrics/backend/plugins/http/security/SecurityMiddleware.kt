package com.stakemetrics.backend.plugins.http.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SecurityMiddleware(private val securityTokenService: SecurityTokenService) : OncePerRequestFilter() {
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
        val email = securityTokenService.validateToken(token)
        val authentication = UsernamePasswordAuthenticationToken(email, null, emptyList())
        SecurityContextHolder.getContext().authentication = authentication
    }
}