package net.stakemetrics.application.workers.enqueuers

import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.service.IQueueService
import net.stakemetrics.application.service.FifaStrategyService
import org.springframework.stereotype.Service

@Service
class FifaStrategyEnqueuer(
    private val queueService: IQueueService,
    private val fifaStrategyService: FifaStrategyService
) {

    fun enqueue(odds: FifaDTO.FifaOddRequest) {
        val strategies = fifaStrategyService.getAllProneToBetStrategies()
        strategies.forEach { strategy ->
            queueService.enqueueCheckOddForStrategyTask(
                FifaDTO.FifaStrategyAgainstOddRequest(
                    strategy,
                    odds
                )
            )
        }
    }

}