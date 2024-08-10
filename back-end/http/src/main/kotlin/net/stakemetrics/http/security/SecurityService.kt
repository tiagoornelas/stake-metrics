package net.stakemetrics.http.security

import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.service.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class SecurityService(private val userService: UserService) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails? {
        return try {
            val queriedUser = userService.findByEmail(email)
            CustomUserDetails(queriedUser)
        } catch (e: NotFoundException) {
            null
        }
    }
}