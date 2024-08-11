package net.stakemetrics.integration.betsapi.utils

import java.util.Date
import net.stakemetrics.integration.betsapi.entities.dtos.BetsApiDTO
import org.springframework.stereotype.Component

@Component
class BetsApiHelper {

    fun hasNext(pager: BetsApiDTO.Pager, page: Int): Boolean {
        val totalFetched = (page - 1) * pager.per_page
        return pager.total > totalFetched
    }

    fun convertTimestampToDate(timestamp: String): Date {
        val unixTimestamp = timestamp.toLong()
        return Date(unixTimestamp * 1000)
    }

}