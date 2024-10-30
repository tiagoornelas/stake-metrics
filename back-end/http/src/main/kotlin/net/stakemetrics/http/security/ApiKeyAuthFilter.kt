package net.stakemetrics.http.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyAuthFilter : OncePerRequestFilter() {

    @Value("\${service.api.key}")
    private val serviceApiKey = ""

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val apiKey = request.getHeader("X-API-KEY")
        if (
            (request.requestURI.startsWith("/queue")
                    || request.requestURI.startsWith("/cron")
                    || request.requestURI.startsWith("/dev")) && (apiKey == null || apiKey != serviceApiKey)
        ) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
            return
        }
        filterChain.doFilter(request, response)
    }
}
