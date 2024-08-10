package net.stakemetrics.application.service


import jakarta.annotation.PostConstruct
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.enums.FifaDefaultLeagues
import org.springframework.stereotype.Service

@Service
class RepositorySeederService(
    private val fifaLeagueService: FifaLeagueService
) {

    @PostConstruct
    fun seed() {
        seedFifaLeagues()
    }

    fun seedFifaLeagues() {
        FifaDefaultLeagues.entries.forEach { fifaLeague ->
            if (!fifaLeagueService.existsByIntegrationId(fifaLeague.id)) {
                fifaLeagueService.save(
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