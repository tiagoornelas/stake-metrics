package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.persistence.models.FifaBetModel
import org.springframework.data.jpa.repository.JpaRepository

interface FifaBetJpaRepository: JpaRepository<FifaBetModel, UUID> {
}