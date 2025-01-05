package net.stakemetrics.persistence.repositories

import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import net.stakemetrics.application.repositories.IFifaMatchRepository
import net.stakemetrics.persistence.jpa.FifaMatchJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class FifaMatchRepository(private val fifaMatchJpaRepository: FifaMatchJpaRepository) : IFifaMatchRepository {

    override fun save(fifaMatch: FifaMatch) {
        fifaMatchJpaRepository.save(fifaMatch.toModel())
    }

    override fun findById(id: UUID): FifaMatch? {
        return fifaMatchJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findLatestMatch(): FifaMatch? {
        val optionalResult = fifaMatchJpaRepository.findTopByStatusOrderByTimeDesc(FifaMatchStatusTypes.ENDED)
        return if (optionalResult.isPresent) optionalResult.get().toDomain() else null
    }

    override fun findLatestMatchForLeague(league: FifaLeague): FifaMatch? {
        val optionalResult = fifaMatchJpaRepository.findTopByLeagueAndStatusOrderByTimeDesc(league.toModel(), FifaMatchStatusTypes.ENDED)
        return if (optionalResult.isPresent) optionalResult.get().toDomain() else null
    }

    override fun existsByIntegrationId(integrationId: Long): Boolean {
        return fifaMatchJpaRepository.existsByIntegrationId(integrationId)
    }

    override fun findByIntegrationId(integrationId: Long): FifaMatch? {
        val optionalResult = fifaMatchJpaRepository.findByIntegrationId(integrationId)
        return if (optionalResult.isPresent) optionalResult.get().toDomain() else null
    }

    override fun listFinishedMatchesByPlayerSince(league: FifaLeague, player: FifaPlayer, since: Date):
            List<FifaMatch> {
        return fifaMatchJpaRepository.findAllByLeagueAndHomeOrAwayAndStatusAndTimeGreaterThan(
            league.toModel(), player.toModel(), player.toModel(),
            FifaMatchStatusTypes.ENDED, since
        ).map { it.toDomain() }
    }

    override fun listFinishedMatchesByMatchupSince(
        league: FifaLeague,
        homePlayer: FifaPlayer,
        awayPlayer: FifaPlayer,
        since: Date
    ): List<FifaMatch> {
        return fifaMatchJpaRepository.findAllByLeagueAndHomeAndAwayAndStatusAndTimeGreaterThan(
            league.toModel(), homePlayer.toModel(), awayPlayer.toModel(),
            FifaMatchStatusTypes.ENDED, since
        ).map { it.toDomain() }
    }
}
