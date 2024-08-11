package net.stakemetrics.persistence.repositories

import java.util.Date
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaPlayer
import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import net.stakemetrics.application.repositories.IFifaMatchRepository
import net.stakemetrics.persistence.jpa.FifaMatchJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class FifaMatchRepository(private val fifaMatchJpaRepository: FifaMatchJpaRepository) : IFifaMatchRepository {

    override fun save(fifaMatch: FifaMatch) {
        fifaMatchJpaRepository.save(fifaMatch.toModel())
    }

    override fun findLatestMatch(): FifaMatch? {
        val optionalResult = fifaMatchJpaRepository.findTopByOrderByTimeDesc()
        return if (optionalResult.isPresent) optionalResult.get().toDomain() else null
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

    override fun listLastFinishedMatchesByPlayer(league: FifaLeague, player: FifaPlayer, last: Int): List<FifaMatch> {
        return fifaMatchJpaRepository.findAllByLeagueAndHomeOrAwayAndStatusOrderByTimeDesc(
            league.toModel(), player.toModel(), player.toModel(),
            FifaMatchStatusTypes.ENDED, PageRequest.of(0, last)
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

    override fun listLastFinishedMatchesByMatchup(
        league: FifaLeague,
        homePlayer: FifaPlayer,
        awayPlayer: FifaPlayer,
        last: Int
    ): List<FifaMatch> {
        return fifaMatchJpaRepository.findAllByLeagueAndHomeAndAwayAndStatusOrderByTimeDesc(
            league.toModel(), homePlayer.toModel(), awayPlayer.toModel(),
            FifaMatchStatusTypes.ENDED, PageRequest.of(0, last)
        ).map { it.toDomain() }
    }
}
