package net.stakemetrics.application.service

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.repositories.IFifaBetRepository
import org.springframework.stereotype.Service

@Service
class FifaBetService(private val fifaBetRepository: IFifaBetRepository) {

    fun save(fifaBet: FifaBet) {
        fifaBetRepository.save(fifaBet)
    }

    fun existsByStrategyAndMatch(fifaStrategy: FifaStrategy, fifaMatch: FifaMatch): Boolean {
        return fifaBetRepository.existsByStrategyAndMatch(fifaStrategy, fifaMatch)
    }
}