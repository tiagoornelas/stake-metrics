package net.stakemetrics.persistence.repositories

import net.stakemetrics.application.repositories.IFifaBetRepository
import net.stakemetrics.persistence.jpa.FifaBetJpaRepository
import org.springframework.stereotype.Repository

@Repository
class FifaBetRepository(private val fifaBetJpaRepository: FifaBetJpaRepository) : IFifaBetRepository {
}