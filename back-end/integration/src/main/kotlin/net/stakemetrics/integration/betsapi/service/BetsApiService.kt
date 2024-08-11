package net.stakemetrics.integration.betsapi.service

import java.util.Calendar
import java.util.Date
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.service.IFifaIntegratedDataSourceService
import net.stakemetrics.integration.betsapi.worker.miner.FifaMatchResultsMiner
import net.stakemetrics.integration.betsapi.worker.miner.FifaUpcomingMatchesMiner
import org.springframework.stereotype.Service

@Service
class BetsApiService(
    private val fifaMatchResultsMiner: FifaMatchResultsMiner,
    private val fifaUpcomingMatchesMiner: FifaUpcomingMatchesMiner
) : IFifaIntegratedDataSourceService {

    override fun getFifaMatchResultsForLeagueSinceDate(league: FifaLeague, date: Date): List<FifaDTO.FifaMatchRequest> {
        val miningDateArrays = getMiningDateRange(date)
        val results = mutableListOf<FifaDTO.FifaMatchRequest>()
        miningDateArrays.forEach { miningDate ->
            results.addAll(fifaMatchResultsMiner.getFifaMarchResultsForDate(league, miningDate))
        }
        return results
    }

    private fun getMiningDateRange(sinceDate: Date): List<Date> {
        val today = Calendar.getInstance().time

        val dateList = mutableListOf<Date>()
        val calendar = Calendar.getInstance().apply {
            time = sinceDate
        }

        while (!calendar.time.after(today)) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dateList
    }

    override fun getUpcomingFifaMatchesWithOddsForLeague(league: FifaLeague): List<FifaDTO.FifaOddRequest> {
        return fifaUpcomingMatchesMiner.getUpcomingFifaMatchesWithOddsForLeague(league)
    }


}
