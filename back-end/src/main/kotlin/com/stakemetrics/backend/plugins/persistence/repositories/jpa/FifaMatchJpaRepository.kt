package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.domain.enums.fifa.FifaMatchStatusTypes
import com.stakemetrics.backend.plugins.persistence.models.FifaLeagueModel
import com.stakemetrics.backend.plugins.persistence.models.FifaMatchModel
import com.stakemetrics.backend.plugins.persistence.models.FifaPlayerModel
import java.util.Date
import java.util.Optional
import java.util.UUID
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository

interface FifaMatchJpaRepository : JpaRepository<FifaMatchModel, UUID> {
    fun findTopByOrderByTimeDesc(): Optional<FifaMatchModel>

    fun findByIntegrationId(integrationId: Int): Optional<FifaMatchModel>

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