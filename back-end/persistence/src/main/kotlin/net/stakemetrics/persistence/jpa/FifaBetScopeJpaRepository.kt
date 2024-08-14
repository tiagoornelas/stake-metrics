package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.persistence.models.FifaBetScopeModel
import org.springframework.data.jpa.repository.JpaRepository

interface FifaBetScopeJpaRepository: JpaRepository<FifaBetScopeModel, UUID> {
}