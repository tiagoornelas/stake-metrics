package net.stakemetrics.application.service

import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.dtos.MessengerDTO

interface IQueueService {
    fun enqueueSaveMatchResultTask(payload: FifaDataSourceDTO.FifaMatchRequest)
    fun enqueueRunStrategyAgainstOddTask(payload: FifaStrategyDTO.FifaStrategyAgainstOddRequest)
    fun enqueueBetTask(payload: FifaBetDTO.BetRequest)
    fun enqueueCloseBetTask(payload: FifaBetDTO.CloseBetRequest)
    fun enqueueMessageTask(payload: MessengerDTO.EnqueueRequest, delay: Int?)
    fun enqueueEditMessageTask(payload: MessengerDTO.EditMessageEnqueueRequest)
}