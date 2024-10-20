package net.stakemetrics.application.workers.enqueuers

import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.repositories.IFifaStrategyRepository
import net.stakemetrics.application.service.IQueueService
import org.springframework.stereotype.Service

@Service
class FifaStrategyAgainstOddsEnqueuer(
    private val queueService: IQueueService,
    private val fifaStrategyRepository: IFifaStrategyRepository,
) {

    fun enqueue(oddSnapshot: FifaOddSnapshot) {
        val strategies = fifaStrategyRepository.getAllProneToBetStrategies()
        strategies.filter { it.leagues.contains(oddSnapshot.fifaMatch.league) }.forEach {
            val request = FifaStrategyDTO.FifaStrategyAgainstOddRequest(it, oddSnapshot)
            queueService.enqueueRunStrategyAgainstOddTask(request)
        }
    }

}