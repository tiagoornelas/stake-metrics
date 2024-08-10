package net.stakemetrics.persistence.jpa

import java.util.UUID
import net.stakemetrics.persistence.models.FifaPlayerModel
import org.springframework.data.jpa.repository.JpaRepository

interface FifaPlayerJpaRepository : JpaRepository<FifaPlayerModel, UUID> {
    fun findByName(name: String): FifaPlayerModel
}