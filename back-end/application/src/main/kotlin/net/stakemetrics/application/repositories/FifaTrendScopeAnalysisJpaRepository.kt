package net.stakemetrics.application.repositories

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface FifaTrendScopeAnalysisJpaRepository : JpaRepository<FifaTrendScopeAnalysisJpaRepository, UUID> {
}