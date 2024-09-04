package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface IFifaBetRepository {
    fun save(fifaBet: FifaBet)
    fun delete(fifaBet: FifaBet)
    fun findById(id: UUID): FifaBet
    fun findOpenBets(): List<FifaBet>
    fun existsByStrategyAndMatch(fifaStrategy: FifaStrategy, fifaMatch: FifaMatch): Boolean
    fun findBetsByStrategyAndFilter(
        userEmail: String,
        strategyId: UUID,
        betFilter: FifaBetDTO.BetFilter,
        pageable: Pageable
    ): Page<FifaBet>
    fun listCumulativeProfits(strategyId: UUID): List<Double>
    fun deleteAllByStrategyId(strategyId: UUID)
}