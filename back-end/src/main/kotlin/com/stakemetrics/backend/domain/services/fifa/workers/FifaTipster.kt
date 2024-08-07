package com.stakemetrics.backend.domain.services.fifa.workers

import com.stakemetrics.backend.domain.entities.fifa.FifaMatch
import com.stakemetrics.backend.domain.entities.fifa.FifaStrategyRule
import com.stakemetrics.backend.domain.enums.fifa.FifaMarketSubTypes
import com.stakemetrics.backend.plugins.http.dto.FifaDTO

interface FifaTipster {
    fun tip(
        marketSubType: FifaMarketSubTypes,
        rules: MutableSet<FifaStrategyRule>,
        odds: List<FifaDTO.FifaGenericOddRequest>,
        results: MutableSet<FifaMatch>
    )
}