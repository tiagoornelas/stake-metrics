package net.stakemetrics.persistence.repositories

import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.persistence.jpa.FifaBetJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.stereotype.Repository

@Repository
class FifaBetRepository(private val fifaBetJpaRepository: FifaBetJpaRepository) : IFifaBetRepository {
    override fun save(fifaBet: FifaBet) {
        fifaBetJpaRepository.save(fifaBet.toModel())
    }

    override fun existsByStrategyAndMatch(fifaStrategy: FifaStrategy, fifaMatch: FifaMatch): Boolean {
        return fifaBetJpaRepository.existsByStrategyAndMatch(fifaStrategy.toModel(), fifaMatch.toModel())
    }

}