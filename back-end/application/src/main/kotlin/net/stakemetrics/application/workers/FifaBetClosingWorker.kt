package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.dtos.MessengerDTO
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import net.stakemetrics.application.service.IQueueService
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.application.workers.tipsters.factory.FifaTipsterFactory
import org.springframework.stereotype.Service

@Service
class FifaBetClosingWorker(
    private val logger: Logger,
    private val queueService: IQueueService,
    private val fifaTipsterFactory: FifaTipsterFactory,
    private val fifaBetMessageBuilder: FifaBetMessageBuilder
) {

    fun close(fifaBet: FifaBet) {
        val matchHasResult = checkIfMatchHasResult(fifaBet)
        if (matchHasResult) {
            closeBet(fifaBet)
        } else {
            logger.log("Could not get result for match ${fifaBet.match?.id}, so the bet cannot be closed yet.")
            return
        }
    }

    private fun checkIfMatchHasResult(fifaBet: FifaBet): Boolean = fifaBet.match?.status == FifaMatchStatusTypes.ENDED

    private fun closeBet(fifaBet: FifaBet) {
        val closer = fifaTipsterFactory.getBetCloser(fifaBet.strategy?.marketType!!)
        val closedBet = closer.closeBet(fifaBet)
        editBetMessages(closedBet)
    }

    private fun editBetMessages(fifaBet: FifaBet) {
        if (fifaBet.status == BetStatusTypes.PENDING || fifaBet.profit == null) {
            logger.log("Could not edit messages for bet ${fifaBet.id} because it is still pending or has no profit.")
            return
        }

        val messages = fifaBet.messages
        messages.forEach { message ->
            queueService.enqueueEditMessageTask(
                MessengerDTO.EditMessageEnqueueRequest(
                    messengerChat = message.messengerChat!!,
                    integrationMessageId = message.integrationMessageId!!,
                    newText = fifaBetMessageBuilder.buildResult(fifaBet)
                )
            )
        }
    }

}