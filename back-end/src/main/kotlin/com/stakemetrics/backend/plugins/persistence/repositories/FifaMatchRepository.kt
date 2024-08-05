package com.stakemetrics.backend.plugins.persistence.repositories

import com.stakemetrics.backend.domain.entities.fifa.FifaLeague
import com.stakemetrics.backend.domain.entities.fifa.FifaMatch
import com.stakemetrics.backend.domain.entities.fifa.FifaPlayer
import com.stakemetrics.backend.domain.enums.fifa.FifaMatchStatusTypes
import com.stakemetrics.backend.domain.ports.fifa.FifaMatchRepositoryPort
import com.stakemetrics.backend.plugins.persistence.models.FifaMatchModel
import com.stakemetrics.backend.plugins.persistence.repositories.jpa.FifaMatchJpaRepository
import java.util.Date
import org.hibernate.query.spi.Limit
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class FifaMatchRepository(private val fifaMatchJpaRepository: FifaMatchJpaRepository) : FifaMatchRepositoryPort {
    override fun save(fifaMatch: FifaMatch) {
        fifaMatchJpaRepository.save(fifaMatch.toModel())
    }

    override fun findLatestMatch(): FifaMatch? {
        val optionalResult = fifaMatchJpaRepository.findTopByOrderByTimeDesc()
        return if (optionalResult.isPresent) optionalResult.get().toDomain() else null
    }

    override fun findByIntegrationId(integrationId: Int): FifaMatch? {
        val optionalResult = fifaMatchJpaRepository.findByIntegrationId(integrationId)
        return if (optionalResult.isPresent) optionalResult.get().toDomain() else null
    }

    override fun listFinishedMatchesByPlayerSince(league: FifaLeague, player: FifaPlayer, since: Date):
            List<FifaMatch> {
        return fifaMatchJpaRepository.findAllByLeagueAndHomeOrAwayAndStatusAndTimeGreaterThan(
            league.toModel(), player.toModel(), player.toModel(),
            FifaMatchStatusTypes.FULL_TIME, since
        ).map { it.toDomain() }
    }

    override fun listLastFinishedMatchesByPlayer(league: FifaLeague, player: FifaPlayer, last: Int): List<FifaMatch> {
        return fifaMatchJpaRepository.findAllByLeagueAndHomeOrAwayAndStatusOrderByTimeDesc(
            league.toModel(), player.toModel(), player.toModel(),
            FifaMatchStatusTypes.FULL_TIME, PageRequest.of(0, last)
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
            FifaMatchStatusTypes.FULL_TIME, since
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
            FifaMatchStatusTypes.FULL_TIME, PageRequest.of(0, last)
        ).map { it.toDomain() }
    }
}

fun FifaMatch.toModel(): FifaMatchModel {
    return FifaMatchModel(
        id,
        integrationId,
        time,
        status,
        league?.toModel(),
        home?.toModel(),
        away?.toModel(),
        homeGoalsAtHalfTime,
        homeGoalsAtFullTime,
        awayGoalsAtHalfTime,
        awayGoalsAtFullTime,
        totalGoalsAtHalfTime,
        totalGoalsAtFullTime,
        winner?.toModel()
    )
}