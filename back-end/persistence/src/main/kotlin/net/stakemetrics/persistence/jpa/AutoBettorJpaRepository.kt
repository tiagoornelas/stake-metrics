package net.stakemetrics.persistence.jpa

import net.stakemetrics.persistence.models.AutoBettorModel
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AutoBettorJpaRepository : JpaRepository<AutoBettorModel, UUID> {
    fun findByUserId(userId: UUID): AutoBettorModel?
    fun deleteByUserId(userId: UUID)
}