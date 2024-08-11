package net.stakemetrics.integration.betsapi.utils

import net.stakemetrics.integration.betsapi.entities.dtos.BetsApiDTO
import org.springframework.stereotype.Component

@Component
class BetsApiHttpHelper {

    fun hasNext(pager: BetsApiDTO.Pager, page: Int): Boolean {
        val totalFetched = (page - 1) * pager.per_page
        return pager.total > totalFetched
    }

}