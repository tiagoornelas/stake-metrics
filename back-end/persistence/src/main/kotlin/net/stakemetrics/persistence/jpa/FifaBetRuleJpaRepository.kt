package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.persistence.models.FifaBetRuleModel
import org.springframework.data.jpa.repository.JpaRepository

interface FifaBetRuleJpaRepository: JpaRepository<FifaBetRuleModel, UUID> {
}