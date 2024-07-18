package com.stakemetrics.backend.domain.services


import com.stakemetrics.backend.domain.entities.FifaLeague
import com.stakemetrics.backend.domain.entities.User
import com.stakemetrics.backend.domain.enums.DefaultFifaLeagues
import com.stakemetrics.backend.domain.enums.UserTypes
import com.stakemetrics.backend.domain.ports.FifaLeagueRepositoryPort
import com.stakemetrics.backend.domain.ports.PasswordEncoderPort
import com.stakemetrics.backend.domain.ports.UserRepositoryPort
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RepositorySeederService(
    @Value("\${service.password}") private val servicePassword: String,
    private val passwordEncoderPort: PasswordEncoderPort,
    private val userRepositoryPort: UserRepositoryPort,
    private val fifaLeagueRepositoryPort: FifaLeagueRepositoryPort
) {

    @PostConstruct
    fun seed() {
        seedServiceAccounts()
        seedFifaLeagues()
    }

    fun seedServiceAccounts() {
        val serviceUsers = listOf(
            User(
                email = "allejo@stakemetrics.net",
                name = "Allejo",
                type = UserTypes.SERVICE,
                password = passwordEncoderPort.encode(servicePassword)
            )
        )

        serviceUsers.forEach { serviceUser ->
            if (!userRepositoryPort.existsByEmail(serviceUser.email)) {
                userRepositoryPort.save(serviceUser)
            }
        }
    }

    fun seedFifaLeagues() {
        DefaultFifaLeagues.entries.forEach { fifaLeague ->
            if (!fifaLeagueRepositoryPort.existsByIntegrationId(fifaLeague.id)) {
                fifaLeagueRepositoryPort.save(
                    FifaLeague(
                        integrationId = fifaLeague.id,
                        name = fifaLeague.nickname,
                        link = fifaLeague.link
                    )
                )
            }
        }
    }
}