package com.saas.backend.plugins.http

import com.saas.backend.domain.ports.PasswordEncoderPort
import com.saas.backend.domain.ports.RecoveryCodeRepositoryPort
import com.saas.backend.domain.ports.SubscriptionServicePort
import com.saas.backend.domain.ports.UserRepositoryPort
import com.saas.backend.domain.services.RecoveryCodeService
import com.saas.backend.domain.services.UserService
import com.saas.backend.plugins.encoder.PasswordEncoderAdapter
import com.saas.backend.plugins.subscription.adapters.SubscriptionServiceAdapter
import com.saas.backend.plugins.subscription.ports.SubscriptionRepositoryPort
import java.util.Locale
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.i18n.SessionLocaleResolver

@Configuration
class SpringGlobalConfig : WebMvcConfigurer {

    @Bean
    fun userServicePort(
        userRepositoryPort: UserRepositoryPort,
        passwordEncoderPort: PasswordEncoderPort,
        subscriptionServicePort: SubscriptionServicePort
    ): UserService {
        return UserService(userRepositoryPort, passwordEncoderPort, subscriptionServicePort)
    }

    @Bean
    fun subscriptionServicePort(
        @Value("\${stripe.api.key}") stripeApiKey: String,
        @Value("\${stripe.pricing.tabe}") stripePricingTableId: String,
        @Value("\${stripe.public.key}") stripePublicKey: String,
        @Value("\${app.frontend.base.url}") appBaseUrl: String,
        subscriptionRepositoryPort: SubscriptionRepositoryPort,
        userRepositoryPort: UserRepositoryPort
    ): SubscriptionServicePort {
        return SubscriptionServiceAdapter(
            stripeApiKey,
            stripePricingTableId,
            stripePublicKey,
            appBaseUrl,
            subscriptionRepositoryPort,
            userRepositoryPort
        )
    }

    @Bean
    fun recoveryCodeServicePort(
        recoveryCodeRepositoryPort: RecoveryCodeRepositoryPort,
        userRepositoryPort: UserRepositoryPort,
        passwordEncoderPort: PasswordEncoderPort
    ): RecoveryCodeService {
        return RecoveryCodeService(recoveryCodeRepositoryPort, userRepositoryPort, passwordEncoderPort)
    }

    @Bean
    fun passwordEncoderPort(bCryptPasswordEncoder: BCryptPasswordEncoder): PasswordEncoderPort {
        return PasswordEncoderAdapter(bCryptPasswordEncoder)
    }

    @Bean
    fun localeResolver(): LocaleResolver {
        val sessionLocaleResolver = SessionLocaleResolver()
        sessionLocaleResolver.setDefaultLocale(Locale.US)
        return sessionLocaleResolver
    }

    @Bean
    fun localeChangeInterceptor(): LocaleChangeInterceptor {
        val localeChangeInterceptor = LocaleChangeInterceptor()
        localeChangeInterceptor.paramName = "lang"
        return localeChangeInterceptor
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeChangeInterceptor())
    }
}