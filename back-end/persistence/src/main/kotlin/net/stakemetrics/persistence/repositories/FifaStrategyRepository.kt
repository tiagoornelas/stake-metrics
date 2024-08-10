package net.stakemetrics.persistence.repositories

import java.util.UUID
import net.stakemetrics.application.entities.FifaStrategy
import net.stakemetrics.application.entities.User
import net.stakemetrics.application.entities.enums.FifaStrategyStatus
import net.stakemetrics.application.repositories.IFifaStrategyRepository
import net.stakemetrics.persistence.jpa.FifaStrategyJpaRepository
import net.stakemetrics.persistence.models.toModel
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@Repository
class FifaStrategyRepository(private val fifaStrategyJpaRepository: FifaStrategyJpaRepository) :
    IFifaStrategyRepository {

    @CacheEvict(value = ["strategies"], allEntries = true)
    override fun save(strategy: FifaStrategy) {
        fifaStrategyJpaRepository.save(strategy.toModel())
    }

    @Cacheable("strategies")
    override fun getAllProneToBetStrategies(): List<FifaStrategy> {
        val proneToBetStatuses = listOf(FifaStrategyStatus.ACTIVE, FifaStrategyStatus.PAPER_BET)
        return fifaStrategyJpaRepository.findAllByStatusIn(proneToBetStatuses).map { it.toDomain() }
    }

    override fun getStrategiesByUser(userId: UUID): List<FifaStrategy> {
        return fifaStrategyJpaRepository.findAllByUserId(userId).map { it.toDomain() }
    }

    override fun findById(id: UUID): FifaStrategy? {
        return fifaStrategyJpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    @CacheEvict(value = ["strategies"], allEntries = true)
    override fun delete(strategy: FifaStrategy) {
        return fifaStrategyJpaRepository.delete(strategy.toModel())
    }

    override fun countByUser(user: User): Int {
        return fifaStrategyJpaRepository.countByUserId(user.id)
    }

    override fun countByUserAndStatus(user: User, status: FifaStrategyStatus): Int {
        return fifaStrategyJpaRepository.countByUserIdAndStatus(user.id, status)
    }

}
