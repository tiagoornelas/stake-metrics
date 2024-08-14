package net.stakemetrics.persistence.repositories

import net.stakemetrics.application.repositories.IFifaBetScopeRepository
import net.stakemetrics.persistence.jpa.FifaBetScopeJpaRepository
import org.springframework.stereotype.Repository

@Repository
class FifaBetScopeRepository(private val fifaBetScopeJpaRepository: FifaBetScopeJpaRepository) :
    IFifaBetScopeRepository {
}