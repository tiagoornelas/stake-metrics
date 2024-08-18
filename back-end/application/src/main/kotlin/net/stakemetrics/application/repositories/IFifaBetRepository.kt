package net.stakemetrics.application.repositories

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaStrategy

interface IFifaBetRepository {
    fun save(fifaBet: FifaBet)
    fun existsByStrategyAndMatchIntegrationId(fifaStrategy: FifaStrategy, matchIntegrationId: Long): Boolean
}