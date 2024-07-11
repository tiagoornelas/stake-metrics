package com.stakemetrics.backend.plugins.http.ports

import com.stakemetrics.backend.domain.entities.FifaLeague
import com.stakemetrics.backend.domain.entities.FifaMatch
import org.springframework.stereotype.Service

@Service
interface FifaServicePort {
    fun listActiveLeagues(): List<FifaLeague>
    fun getLastResultTime(): Long
    fun saveMatch(
        integrationId: Int,
        time: Int,
        status: Int,
        leagueId: Int,
        home: String,
        away: String,
        homeGoalsAtHalfTime: Int?,
        homeGoalsAtFullTime: Int?,
        awayGoalsAtHalfTime: Int?,
        awayGoalsAtFullTime: Int?,
        totalGoalsAtHalfTime: Int?,
        totalGoalsAtFullTime: Int?,
        winner: String?
    )
}