package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
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

    override fun existsByStrategyAndMatchIntegrationId(
        fifaStrategy: FifaStrategy, matchIntegrationId: Long
    ): Boolean {
        return fifaBetJpaRepository.existsByStrategyAndMatchIntegrationId(fifaStrategy.toModel(), matchIntegrationId)
    }

    override fun listAllByStrategyIds(strategyIds: Collection<UUID>): List<FifaBet> {
        return fifaBetJpaRepository.findAllByStrategyIdIn(strategyIds).map { it.toDomain() }
    }

}