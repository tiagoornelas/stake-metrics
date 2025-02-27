package net.stakemetrics.persistence.jpa

import net.stakemetrics.persistence.models.FifaTrendScopeAnalysisModel
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FifaTrendScopeAnalysisJpaRepository : JpaRepository<FifaTrendScopeAnalysisModel, UUID> {
}