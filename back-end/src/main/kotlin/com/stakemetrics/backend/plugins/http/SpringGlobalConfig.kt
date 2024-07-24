package com.stakemetrics.backend.plugins.http

import com.stakemetrics.backend.domain.ports.EmailSenderPort
import com.stakemetrics.backend.domain.ports.PasswordEncoderPort
import com.stakemetrics.backend.domain.ports.RecoveryCodeRepositoryPort
import com.stakemetrics.backend.domain.ports.SubscriptionServicePort
import com.stakemetrics.backend.domain.ports.UserRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaLeagueRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaMatchRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaPlayerRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaRuleRepositoryPort
import com.stakemetrics.backend.domain.ports.fifa.FifaStrategyRepositoryPort
import com.stakemetrics.backend.domain.services.RecoveryCodeService
import com.stakemetrics.backend.domain.services.RepositorySeederService
import com.stakemetrics.backend.domain.services.UserService
import com.stakemetrics.backend.domain.services.fifa.FifaService
import com.stakemetrics.backend.plugins.email.EmailSender
import com.stakemetrics.backend.plugins.encoder.PasswordEncoder
import com.stakemetrics.backend.plugins.http.ports.FifaServicePort
import com.stakemetrics.backend.plugins.http.ports.TelegramServicePort
import com.stakemetrics.backend.plugins.http.ports.UserServicePort
import com.stakemetrics.backend.plugins.persistence.repositories.FifaLeagueRepository
import com.stakemetrics.backend.plugins.subscription.ports.SubscriptionRepositoryPort
import com.stakemetrics.backend.plugins.subscription.service.SubscriptionService
import com.stakemetrics.backend.plugins.telegram.TelegramService
import com.stakemetrics.backend.plugins.telegram.ports.TelegramChatRepositoryPort
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
        @Value("\${stripe.pricing.table}") stripePricingTableId: String,
        @Value("\${stripe.public.key}") stripePublicKey: String,
        @Value("\${app.frontend.base.url}") appBaseUrl: String,
        subscriptionRepositoryPort: SubscriptionRepositoryPort,
        userRepositoryPort: UserRepositoryPort
    ): SubscriptionServicePort {
        return SubscriptionService(
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
        passwordEncoderPort: PasswordEncoderPort,
        emailSenderPort: EmailSenderPort
    ): RecoveryCodeService {
        return RecoveryCodeService(recoveryCodeRepositoryPort, userRepositoryPort, passwordEncoderPort, emailSenderPort)
    }

    @Bean
    fun passwordEncoderPort(bCryptPasswordEncoder: BCryptPasswordEncoder): PasswordEncoderPort {
        return PasswordEncoder(bCryptPasswordEncoder)
    }

    @Bean
    fun emailSenderPort(
        @Value("\${mailersend.api.key}") mailerSendApiKey: String,
        @Value("\${mailersend.domain.email}") domainEmail: String
    ): EmailSenderPort {
        return EmailSender(mailerSendApiKey, domainEmail)
    }

    @Bean
    fun telegramServicePort(
        @Value("\${telegram.bot.token}") botToken: String,
        telegramChatRepositoryPort: TelegramChatRepositoryPort,
        userServicePort: UserServicePort
    ): TelegramServicePort {
        return TelegramService(botToken, telegramChatRepositoryPort, userServicePort)
    }

    @Bean
    fun fifaServicePort(
        userService: UserService,
        fifaLeagueRepositoryPort: FifaLeagueRepositoryPort,
        fifaMatchRepositoryPort: FifaMatchRepositoryPort,
        fifaPlayerRepositoryPort: FifaPlayerRepositoryPort,
        fifaStrategyRepositoryPort: FifaStrategyRepositoryPort,
        fifaRuleRepositoryPort: FifaRuleRepositoryPort
    ): FifaServicePort {
        return FifaService(
            userService,
            fifaLeagueRepositoryPort,
            fifaMatchRepositoryPort,
            fifaPlayerRepositoryPort,
            fifaStrategyRepositoryPort,
            fifaRuleRepositoryPort
        )
    }

    @Bean
    fun localeResolver(): LocaleResolver {
        val sessionLocaleResolver = SessionLocaleResolver()
        sessionLocaleResolver.setDefaultLocale(Locale.US)
        return sessionLocaleResolver
    }

    @Bean
    fun repositorySeederService(
        @Value("\${service.password}") servicePassword: String,
        passwordEncoderPort: PasswordEncoderPort,
        userRepositoryPort: UserRepositoryPort,
        fifaLeagueRepository: FifaLeagueRepository
    ): RepositorySeederService {
        return RepositorySeederService(servicePassword, passwordEncoderPort, userRepositoryPort, fifaLeagueRepository)
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