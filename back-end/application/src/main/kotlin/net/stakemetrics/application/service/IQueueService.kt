package net.stakemetrics.application.service

import net.stakemetrics.application.entities.dtos.FifaDTO

interface IQueueService {
    fun enqueueSaveMatchResultTask(match: FifaDTO.FifaMatchRequest)
    fun enqueueCheckOddForStrategyTask(payload: FifaDTO.FifaStrategyAgainstOddRequest)
    fun enqueueSendMessageTask(payload: Any)
}