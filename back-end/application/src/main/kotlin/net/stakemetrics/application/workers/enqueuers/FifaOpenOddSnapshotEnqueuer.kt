package net.stakemetrics.application.workers.enqueuers

import net.stakemetrics.application.entities.enums.FifaMatchStatusTypes
import net.stakemetrics.application.repositories.IFifaOddSnapshotRepository
import net.stakemetrics.application.service.IQueueService
import org.springframework.stereotype.Service

@Service
class FifaOpenOddSnapshotEnqueuer(
    private val fifaOddSnapshotRepository: IFifaOddSnapshotRepository,
    private val queueService: IQueueService
) {

    fun enqueue() {
        val pendingOddSnapshots = fifaOddSnapshotRepository.findAllPending()
        pendingOddSnapshots
            .filter { it.fifaMatch.status == FifaMatchStatusTypes.ENDED }
            .forEach(queueService::enqueueCloseOddSnapshotTask)
    }

}