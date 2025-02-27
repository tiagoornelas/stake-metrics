package net.stakemetrics.integration.betsapi

import java.util.Calendar
import java.util.Date
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.service.IFifaIntegratedDataSourceService
import net.stakemetrics.integration.betsapi.worker.miner.FifaMatchResultsMiner
import net.stakemetrics.integration.betsapi.worker.miner.FifaUpcomingMatchesMiner
import org.springframework.stereotype.Service

@Service
class BetsApiService(
    private val fifaMatchResultsMiner: FifaMatchResultsMiner,
    private val fifaUpcomingMatchesMiner: FifaUpcomingMatchesMiner
) : IFifaIntegratedDataSourceService {

    override fun getFifaMatchResultsForLeagueSinceDate(
        league: FifaLeague,
        date: Date
    ): List<FifaDataSourceDTO.FifaMatchRequest> {
        val miningDateArrays = getMiningDateRange(date)
        val results = mutableListOf<FifaDataSourceDTO.FifaMatchRequest>()
        miningDateArrays.forEach { miningDate ->
            results.addAll(fifaMatchResultsMiner.getFifaMarchResultsForDate(league, miningDate))
        }
        return results
    }

    private fun getMiningDateRange(sinceDate: Date): List<Date> {
        val today = Calendar.getInstance().apply {
            setToMidnight(this)
        }.time

        val dateList = mutableListOf<Date>()
        val calendar = Calendar.getInstance().apply {
            time = sinceDate
            setToMidnight(this)
        }

        while (!calendar.time.after(today)) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dateList
    }

    private fun setToMidnight(calendar: Calendar) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    override fun getUpcomingFifaMatchesWithOddsForLeague(league: FifaLeague): List<FifaDataSourceDTO.FifaOddRequest> {
        return fifaUpcomingMatchesMiner.getUpcomingFifaMatchesWithOddsForLeague(league)
    }

}
