package net.stakemetrics.application.workers.enqueuers

import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.application.service.IQueueService
import org.springframework.stereotype.Service

@Service
class FifaOpenBetsEnqueuer(private val fifaBetRepository: IFifaBetRepository, private val queueService: IQueueService) {

    fun enqueue() {
        val openBets = fifaBetRepository.findOpenBets()
        openBets.filter { bet -> bet.match?.status == FifaMatchStatusTypes.ENDED }
            .forEach { bet ->
                val payload = FifaBetDTO.CloseBetRequest(bet)
                queueService.enqueueCloseBetTask(payload)
            }
    }

}