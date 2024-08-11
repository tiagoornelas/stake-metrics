package net.stakemetrics.persistence.jpa

import java.util.Date
import java.util.Optional
import java.util.UUID
import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import net.stakemetrics.persistence.models.FifaLeagueModel
import net.stakemetrics.persistence.models.FifaMatchModel
import net.stakemetrics.persistence.models.FifaPlayerModel
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository

interface FifaMatchJpaRepository : JpaRepository<FifaMatchModel, UUID> {
    fun findTopByOrderByTimeDesc(): Optional<FifaMatchModel>

    fun findByIntegrationId(integrationId: Long): Optional<FifaMatchModel>

    fun findAllByLeagueAndHomeOrAwayAndStatusAndTimeGreaterThan(
        league: FifaLeagueModel, home: FifaPlayerModel, away: FifaPlayerModel, status: FifaMatchStatusTypes, time:
        Date
    ): List<FifaMatchModel>

    fun findAllByLeagueAndHomeAndAwayAndStatusAndTimeGreaterThan(
        league: FifaLeagueModel, home: FifaPlayerModel, away: FifaPlayerModel, status: FifaMatchStatusTypes, time:
        Date
    ): List<FifaMatchModel>

    fun findAllByLeagueAndHomeOrAwayAndStatusOrderByTimeDesc(
        league: FifaLeagueModel, home: FifaPlayerModel, away: FifaPlayerModel, status:
        FifaMatchStatusTypes, pageable: PageRequest
    ): List<FifaMatchModel>

    fun findAllByLeagueAndHomeAndAwayAndStatusOrderByTimeDesc(
        league: FifaLeagueModel, home: FifaPlayerModel, away: FifaPlayerModel, status:
        FifaMatchStatusTypes, pageable: PageRequest
    ): List<FifaMatchModel>
}