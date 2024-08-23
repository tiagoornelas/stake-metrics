package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaBet
import org.springframework.stereotype.Service

@Service
class FifaBetCloser {

    fun close(fifaBet: FifaBet) {
        println("Closing bet: ${fifaBet.id}")
    }
}