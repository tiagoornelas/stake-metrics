package net.stakemetrics.application.workers.enqueuers

import net.stakemetrics.application.service.FifaLeagueService
import net.stakemetrics.application.service.IFifaIntegratedDataSourceService
import net.stakemetrics.application.service.IQueueService
import org.springframework.stereotype.Service

@Service
class FifaUpcomingMatchesEnqueuer(
    private val queueService: IQueueService,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaIntegratedDataSourceRepository: IFifaIntegratedDataSourceService
) {

    fun enqueue() {
        val leagues = fifaLeagueService.listActiveLeagues()

        leagues.forEach { league ->
            val nextMatchesOdds = fifaIntegratedDataSourceRepository.getUpcomingFifaMatchesWithOddsForLeague(league)
            nextMatchesOdds.forEach(queueService::enqueueRunTrendAnalysisTask)
        }
    }

}