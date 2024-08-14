package net.stakemetrics.persistence.repositories

import net.stakemetrics.application.repositories.IFifaBetRuleRepository
import net.stakemetrics.persistence.jpa.FifaBetRuleJpaRepository
import org.springframework.stereotype.Repository

@Repository
class FifaBetRuleRepository(private val fifaBetRuleJpaRepository: FifaBetRuleJpaRepository) : IFifaBetRuleRepository {
}