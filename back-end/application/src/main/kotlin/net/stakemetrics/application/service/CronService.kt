package net.stakemetrics.application.service

import net.stakemetrics.application.workers.enqueuers.FifaUpcomingMatchesEnqueuer
import net.stakemetrics.application.workers.enqueuers.FifaEndedMatchesEnqueuer
import org.springframework.stereotype.Service

@Service
class CronService(
    private val fifaEndedMatchesEnqueuer: FifaEndedMatchesEnqueuer,
    private val fifaUpcomingMatchesEnqueuer: FifaUpcomingMatchesEnqueuer
) {

    fun mineFifaMatchResults() {
        fifaEndedMatchesEnqueuer.mine()
    }

    fun mineFifaMatchOdds() {
        fifaUpcomingMatchesEnqueuer.mine()
    }
}