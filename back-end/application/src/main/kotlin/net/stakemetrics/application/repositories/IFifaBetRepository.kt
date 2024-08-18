package net.stakemetrics.application.repositories

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategy

interface IFifaBetRepository {
    fun save(fifaBet: FifaBet)
    fun existsByStrategyAndMatch(fifaStrategy: FifaStrategy, fifaMatch: FifaMatch): Boolean
}