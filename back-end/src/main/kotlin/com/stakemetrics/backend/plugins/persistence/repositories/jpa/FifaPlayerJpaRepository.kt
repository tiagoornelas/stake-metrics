package com.stakemetrics.backend.plugins.persistence.repositories.jpa

import com.stakemetrics.backend.plugins.persistence.models.FifaPlayerModel
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface FifaPlayerJpaRepository: JpaRepository<FifaPlayerModel, UUID> {
    fun findByName(name: String): FifaPlayerModel?
}