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
import java.time.LocalDate
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

    override fun countOpenBetsByUser(userId: UUID): Int {
        return fifaBetJpaRepository.countByStatusAndUserId(BetStatusTypes.PENDING, userId)
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
        messengerChatId: UUID, startDate: LocalDateTime, endDate: LocalDateTime
    ): List<FifaBet> {
        return fifaBetJpaRepository.findByMessengerChatAndDateBetween(
            messengerChatId, startDate, endDate
        ).map { it.toDomain() }
    }

    override fun getMainStatisticsByUserAndDateBetween(
        userId: UUID, startDate: LocalDateTime, endDate: LocalDateTime
    ): FifaBetDTO.MainStatistics {
        return fifaBetJpaRepository.getMainStatisticsByUserAndDateBetween(
            userId, startDate, endDate
        ).toMainStatistics()
    }

    private fun FifaBetJpaRepository.MainStatisticsProjection.toMainStatistics() = FifaBetDTO.MainStatistics(
        numberOfBets = numberOfBets,
        profit = profit ?: 0.0,
        roi = roi ?: 0.0
    )

    override fun getMonthlyProfits(
        userId: UUID, timezone: String, startDate: LocalDate, endDate: LocalDate
    ): List<FifaBetDTO.MonthlyProfit> {
        return fifaBetJpaRepository.getMonthlyProfits(userId, timezone, startDate, endDate)
            .map { FifaBetDTO.MonthlyProfit(it.startDate, it.endDate, it.profit) }
    }

    override fun getDailyProfits(
        userId: UUID, timezone: String, startDate: LocalDate, endDate: LocalDate
    ): List<FifaBetDTO.DailyProfit> {
        return fifaBetJpaRepository.getDailyProfits(userId, timezone, startDate, endDate)
            .map { FifaBetDTO.DailyProfit(it.date, it.profit) }
    }

    override fun getPossibleProfitFromOpenBets(userId: UUID): Double {
        return fifaBetJpaRepository.getPossibleProfitFromOpenBets(userId)
    }
}