package net.stakemetrics.application.service

import java.util.Date
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO

interface IFifaIntegratedDataSourceService {
    fun getFifaMatchResultsForLeagueSinceDate(league: FifaLeague, date: Date): List<FifaDataSourceDTO.FifaMatchRequest>
    fun getUpcomingFifaMatchesWithOddsForLeague(league: FifaLeague): List<FifaDataSourceDTO.FifaOddRequest>
}