package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.persistence.jpa.FifaBetJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class FifaBetRepository(private val fifaBetJpaRepository: FifaBetJpaRepository) : IFifaBetRepository {
    override fun save(fifaBet: FifaBet) {
        fifaBetJpaRepository.save(fifaBet.toModel())
    }

    override fun delete(fifaBet: FifaBet) {
        fifaBetJpaRepository.delete(fifaBet.toModel())
    }

    override fun findById(id: UUID): FifaBet {
        return fifaBetJpaRepository.findById(id).map { it.toDomain() }
            .orElseThrow { NotFoundException("FifaBet", "id", id.toString()) }
    }

    override fun findOpenBets(): List<FifaBet> {
        return fifaBetJpaRepository.findAllByStatusOrProfit(BetStatusTypes.PENDING, null).map { it.toDomain() }
    }

    override fun existsByStrategyAndMatchIntegrationId(
        fifaStrategy: FifaStrategy, matchIntegrationId: Long
    ): Boolean {
        return fifaBetJpaRepository.existsByStrategyAndMatchIntegrationId(fifaStrategy.toModel(), matchIntegrationId)
    }

    override fun listAllByStrategyIds(strategyIds: Collection<UUID>, page: Int, size: Int): Page<FifaBet> {
        val pageable = PageRequest.of(page, size)
        return fifaBetJpaRepository.findAllByStrategyIdInOrderByMatchTimeDesc(strategyIds, pageable)
            .map { it.toDomain() }
    }

}