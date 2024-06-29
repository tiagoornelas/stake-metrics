package com.saas.backend.plugins.http.security

import com.saas.backend.plugins.http.ports.UserServicePort
import com.saas.backend.plugins.persistence.adapters.toModel
import com.saas.backend.plugins.persistence.models.UserModel
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class SecurityService(private val userServicePort: UserServicePort) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserModel {
        val queriedUser = userServicePort.findByEmail(email)
        return queriedUser.toModel()
    }
}