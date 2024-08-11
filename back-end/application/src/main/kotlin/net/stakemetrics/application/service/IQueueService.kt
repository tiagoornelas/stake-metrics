package net.stakemetrics.application.service

import net.stakemetrics.application.entities.dtos.FifaDTO

interface IQueueService {
    fun enqueueSaveMatchResultTask(payload: FifaDTO.FifaMatchRequest)
    fun enqueueRunStrategyAgainstOddTask(payload: FifaDTO.FifaStrategyAgainstOddRequest)
    fun enqueueSendMessageTask(payload: Any)
}