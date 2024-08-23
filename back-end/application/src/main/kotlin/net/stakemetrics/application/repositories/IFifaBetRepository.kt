package net.stakemetrics.application.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaStrategy
import org.springframework.data.domain.Page

interface IFifaBetRepository {
    fun save(fifaBet: FifaBet)
    fun delete(fifaBet: FifaBet)
    fun findById(id: UUID): FifaBet
    fun findOpenBets(): List<FifaBet>
    fun existsByStrategyAndMatchIntegrationId(fifaStrategy: FifaStrategy, matchIntegrationId: Long): Boolean
    fun listAllByStrategyIds(strategyIds: Collection<UUID>, page: Int, size: Int): Page<FifaBet>
}