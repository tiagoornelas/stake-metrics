package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.application.workers.tipsters.factory.FifaTipsterFactory
import org.springframework.stereotype.Service

@Service
class FifaBetClosingWorker(
    private val logger: Logger,
    private val fifaTipsterFactory: FifaTipsterFactory
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
        closer.closeBet(fifaBet)
    }

}