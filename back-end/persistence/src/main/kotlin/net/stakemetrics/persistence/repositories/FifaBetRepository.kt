package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.exceptions.NotFoundException
import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.persistence.jpa.FifaBetJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

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

    override fun existsByStrategyAndMatch(fifaStrategy: FifaStrategy, fifaMatch: FifaMatch): Boolean {
        return fifaBetJpaRepository.existsByStrategyAndMatch(fifaStrategy.toModel(), fifaMatch.toModel())
    }

    override fun findBetsByStrategyAndFilter(
        userEmail: String, strategyId: UUID, betFilter: FifaBetDTO.BetFilter, pageable: Pageable
    ): Page<FifaBet> {
        return fifaBetJpaRepository.findBetsByStrategyAndFilter(strategyId, betFilter, pageable).map { it.toDomain() }
    }

    override fun listCumulativeProfits(strategyId: UUID): List<Double> {
        return fifaBetJpaRepository.findCumulativeProfitsByStrategyId(strategyId)
    }

    override fun deleteAllByStrategyId(strategyId: UUID) {
        fifaBetJpaRepository.deleteAllByStrategyId(strategyId)
    }

    override fun findByMessengerChatAndDateBetween(
        messengerChatId: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<FifaBet> {
        return fifaBetJpaRepository.findByMessengerChatAndDateBetween(
            messengerChatId, startDate, endDate
        ).map { it.toDomain() }
    }
}