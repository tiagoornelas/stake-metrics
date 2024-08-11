package net.stakemetrics.application.workers.enqueuers

import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.service.FifaLeagueService
import net.stakemetrics.application.service.FifaStrategyService
import net.stakemetrics.application.service.IFifaIntegratedDataSourceService
import net.stakemetrics.application.service.IQueueService
import org.springframework.stereotype.Service

@Service
class FifaUpcomingMatchesEnqueuer(
    private val queueService: IQueueService,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaStrategyService: FifaStrategyService,
    private val fifaIntegratedDataSourceRepository: IFifaIntegratedDataSourceService
) {

    fun mine() {
        val leagues = fifaLeagueService.listActiveLeagues()
        val strategies = fifaStrategyService.getAllProneToBetStrategies()

        val requests = leagues.flatMap { league ->
            val nextMatchesOdds = fifaIntegratedDataSourceRepository.getUpcomingFifaMatchesWithOddsForLeague(league)
            nextMatchesOdds.flatMap { odds ->
                strategies.filter { it.leagues.contains(league) }
                    .map { strategy -> FifaDTO.FifaStrategyAgainstOddRequest(strategy, odds) }
            }
        }

        val requestsWithAvailableOdds = requests.filter { it.odds.odds.isNotEmpty() }

        requestsWithAvailableOdds.forEach(queueService::enqueueRunStrategyAgainstOddTask)
    }
}