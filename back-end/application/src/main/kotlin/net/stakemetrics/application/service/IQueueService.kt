package net.stakemetrics.application.service

import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO

interface IQueueService {
    fun enqueueSaveMatchResultTask(payload: FifaDataSourceDTO.FifaMatchRequest)
    fun enqueueRunStrategyAgainstOddTask(payload: FifaStrategyDTO.FifaStrategyAgainstOddRequest)
    fun enqueueSendMessageTask(payload: Any)
}