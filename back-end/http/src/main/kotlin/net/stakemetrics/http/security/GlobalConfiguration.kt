package net.stakemetrics.http.security

import net.stakemetrics.application.entities.annotations.EnvironmentSensitive
import net.stakemetrics.application.utils.EnvironmentVerifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class GlobalConfiguration(
    val securityMiddleware: SecurityMiddleware,
    val environmentVerifier: EnvironmentVerifier
) {

    @Bean
    @EnvironmentSensitive
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { csrf -> csrf.disable() }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers(HttpMethod.POST, "/user").permitAll()
                    .requestMatchers(HttpMethod.POST, "/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/recover").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/recover").permitAll()
                    .apply {
                        if (!environmentVerifier.isProd()) {
                            requestMatchers(HttpMethod.POST, "/queue/**").permitAll()
                            requestMatchers(HttpMethod.PUT, "/queue/**").permitAll()
                            requestMatchers(HttpMethod.POST, "/cron/**").permitAll()
                        }
                    }
                    .anyRequest().authenticated()
            }
            .addFilterBefore(securityMiddleware, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

}
