package net.stakemetrics.application.workers.enqueuers

import net.stakemetrics.application.entities.FifaLeague
import java.util.Calendar
import java.util.Date
import net.stakemetrics.application.service.IFifaIntegratedDataSourceService
import net.stakemetrics.application.service.FifaLeagueService
import net.stakemetrics.application.service.FifaMatchService
import net.stakemetrics.application.service.IQueueService
import org.springframework.stereotype.Service

@Service
class FifaEndedMatchesEnqueuer(
    private val queueService: IQueueService,
    private val fifaMatchService: FifaMatchService,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaIntegratedDataSourceService: IFifaIntegratedDataSourceService
) {

    fun mine() {
        val activeLeagues = fifaLeagueService.listActiveLeagues()

        activeLeagues.forEach { league ->
            val sinceDate = getLastMinedDate(league)
            val pastResults = fifaIntegratedDataSourceService.getFifaMatchResultsForLeagueSinceDate(league, sinceDate)

            pastResults
                .filter { !fifaMatchService.hasMatchSavedWithResult(it.integrationId) }
                .forEach(queueService::enqueueSaveMatchResultTask)
        }
    }

    private fun getLastMinedDate(league: FifaLeague): Date {
        val fetchSince = fifaMatchService.getLastMatchResultTimeForLeague(league)
        val calendar = Calendar.getInstance().apply { time = fetchSince }
        val isBeforeSixAM = calendar.get(Calendar.HOUR_OF_DAY) < 6
        if (isBeforeSixAM) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return calendar.time
    }
}