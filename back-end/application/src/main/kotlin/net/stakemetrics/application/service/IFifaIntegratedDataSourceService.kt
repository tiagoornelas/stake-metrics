package net.stakemetrics.application.service

import java.util.Date
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.dtos.FifaDTO

interface IFifaIntegratedDataSourceService {
    fun getFifaMatchResultsForLeagueSinceDate(league: FifaLeague, date: Date): List<FifaDTO.FifaMatchRequest>
    fun getUpcomingFifaMatchesWithOddsForLeague(league: FifaLeague): List<FifaDTO.FifaOddRequest>
}