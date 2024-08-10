package net.stakemetrics.application.workers.miners

import java.util.Calendar
import java.util.Date
import net.stakemetrics.application.service.IFifaIntegratedDataSourceService
import net.stakemetrics.application.service.FifaLeagueService
import net.stakemetrics.application.service.FifaMatchService
import net.stakemetrics.application.service.IQueueService
import org.springframework.stereotype.Service

@Service
class FifaPastResultsMiner(
    private val queueService: IQueueService,
    private val fifaMatchService: FifaMatchService,
    private val fifaLeagueService: FifaLeagueService,
    private val fifaIntegratedDataSourceRepository: IFifaIntegratedDataSourceService
) {

    fun mine() {
        val activeLeagues = fifaLeagueService.listActiveLeagues()
        val sinceDate = getLastMinedDate()

        activeLeagues.forEach { league ->
            val pastResults =
                fifaIntegratedDataSourceRepository.getFifaMatchResultsForLeagueSinceDate(league, sinceDate)
            pastResults.forEach(queueService::enqueueSaveMatchResultTask)
        }
    }

    private fun getLastMinedDate(): Date {
        val fetchSince = fifaMatchService.getLastMatchResultTime()
        val calendar = Calendar.getInstance().apply {
            time = fetchSince
        }

        val isBeforeSixAM = calendar.get(Calendar.HOUR_OF_DAY) < 6
        if (isBeforeSixAM) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        return calendar.time
    }
}