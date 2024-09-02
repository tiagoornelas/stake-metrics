package net.stakemetrics.application.service

import net.stakemetrics.application.workers.UserSubscriptionChecker
import net.stakemetrics.application.workers.enqueuers.FifaEndedMatchesEnqueuer
import net.stakemetrics.application.workers.enqueuers.FifaOpenBetsEnqueuer
import net.stakemetrics.application.workers.enqueuers.FifaOpenOddSnapshotEnqueuer
import net.stakemetrics.application.workers.enqueuers.FifaUpcomingMatchesEnqueuer
import org.springframework.stereotype.Service

@Service
class CronService(
    private val fifaOpenBetsEnqueuer: FifaOpenBetsEnqueuer,
    private val userSubscriptionChecker: UserSubscriptionChecker,
    private val fifaEndedMatchesEnqueuer: FifaEndedMatchesEnqueuer,
    private val fifaOpenOddSnapshotEnqueuer: FifaOpenOddSnapshotEnqueuer,
    private val fifaUpcomingMatchesEnqueuer: FifaUpcomingMatchesEnqueuer
) {

    fun mineFifaMatchResults() {
        fifaEndedMatchesEnqueuer.mine()
    }

    fun closeFifaMatchBets() {
        fifaOpenBetsEnqueuer.enqueue()
    }
    
    fun closeOddSnapshots() {
        fifaOpenOddSnapshotEnqueuer.enqueue()
    }

    fun mineFifaMatchOdds() {
        fifaUpcomingMatchesEnqueuer.enqueue()
    }

    fun checkUserSubscriptions() {
        userSubscriptionChecker.check()
    }

}