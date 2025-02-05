package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.dtos.FifaBetDTO
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
        if (fifaBet.isHanging()) {
            logger.log("Match ${fifaBet.match?.id} has been hanging for too long, discarding bet.")
            discardHangingBet(fifaBet)
            return
        }

        if (!checkIfMatchHasResult(fifaBet)) {
            logger.log("Could not get result for match ${fifaBet.match?.id}, so the bet cannot be closed yet.")
            return
        }

        closeBet(fifaBet)
    }

    private fun discardHangingBet(fifaBet: FifaBet) {
        fifaBet.match?.let { match ->
            logger.log("Discarding hanging bet ${fifaBet.id} for match ${match.id} that started at ${match.time}")
            val payload = FifaBetDTO.CloseBetRequest(fifaBet)
            queueService.enqueueDiscardHangingBetTask(payload)
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
        val integratedMessages = messages.filter { it.integrationMessageId != null }
        integratedMessages.forEach { message ->
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