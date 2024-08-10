package net.stakemetrics.application.utils

import net.stakemetrics.application.entities.annotations.EnvironmentSensitive
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@EnvironmentSensitive
class EnvironmentVerifier {
    @Value("\${spring.profiles.active}")
    private val environment: String? = null

    fun isProd(): Boolean {
        return environment == "prod"
    }
}