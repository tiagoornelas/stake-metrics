package com.saas.backend.plugins.http.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class GlobalConfiguration(
    val authenticationConfiguration: AuthenticationConfiguration,
    val securityMiddleware: SecurityMiddleware
) {

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return authenticationConfiguration.getAuthenticationManager()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { csrf -> csrf.disable() }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers(HttpMethod.POST, "/user").permitAll()
                    .requestMatchers(HttpMethod.POST, "/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/recover").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/recover").permitAll()
                    .requestMatchers(HttpMethod.POST, "/subscription/notify-event").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(securityMiddleware, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

}
