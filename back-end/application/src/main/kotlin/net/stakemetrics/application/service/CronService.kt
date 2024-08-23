package net.stakemetrics.application.service

import net.stakemetrics.application.workers.enqueuers.FifaEndedMatchesEnqueuer
import net.stakemetrics.application.workers.enqueuers.FifaOpenBetsEnqueuer
import net.stakemetrics.application.workers.enqueuers.FifaUpcomingMatchesEnqueuer
import org.springframework.stereotype.Service

@Service
class CronService(
    private val fifaOpenBetsEnqueuer: FifaOpenBetsEnqueuer,
    private val fifaEndedMatchesEnqueuer: FifaEndedMatchesEnqueuer,
    private val fifaUpcomingMatchesEnqueuer: FifaUpcomingMatchesEnqueuer
) {

    fun mineFifaMatchResults() {
        fifaEndedMatchesEnqueuer.mine()
    }

    fun mineFifaMatchOdds() {
        fifaUpcomingMatchesEnqueuer.mine()
    }

    fun closeFifaBets() {
        fifaOpenBetsEnqueuer.enqueue()
    }
}