package net.stakemetrics.application.service

import net.stakemetrics.application.workers.miners.FifaPastResultsMiner
import org.springframework.stereotype.Service

@Service
class CronService(private val fifaPastResultsMiner: FifaPastResultsMiner) {

    fun mineFifaMatchResults() {
        fifaPastResultsMiner.mine()
    }
}