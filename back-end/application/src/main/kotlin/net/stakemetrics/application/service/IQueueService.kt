package net.stakemetrics.application.service

import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.dtos.MessengerDTO

interface IQueueService {
    fun enqueueSaveMatchResultTask(payload: FifaDataSourceDTO.FifaMatchRequest)
    fun enqueueRunStrategyAgainstOddTask(payload: FifaStrategyDTO.FifaStrategyAgainstOddRequest)
    fun enqueueBetTask(payload: FifaBetDTO.BetRequest)
    fun enqueueMessageTask(payload: MessengerDTO.EnqueueRequest, delay: Int?)
}