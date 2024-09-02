package net.stakemetrics.persistence.models

import jakarta.persistence.*
import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.FifaOddSnapshot
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.enums.FifaMarketSubTypes
import net.stakemetrics.application.entities.enums.OddSnapshotTypes

@Entity
@Table(name = "fifa_odd_snapshot")
data class FifaOddSnapshotModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "match_id")
    val fifaMatch: FifaMatchModel? = null,
    @Embedded
    val value: FifaDataSourceDTO.FifaGenericOddRequest? = null,
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    val trendScopeAnalysis: MutableSet<FifaTrendScopeAnalysisModel> = mutableSetOf(),
    val status: OddSnapshotTypes = OddSnapshotTypes.PENDING,
    val homeProfit: Double? = null,
    val drawProfit: Double? = null,
    val awayProfit: Double? = null,
    val overProfit: Double? = null,
    val underProfit: Double? = null,
    val matchOddsWinnerSubType: FifaMarketSubTypes? = null,
    val goalLineWinnerSubType: FifaMarketSubTypes? = null,
    val createdAt: Date = Date()
) {
    fun toDomain(): FifaOddSnapshot {
        return FifaOddSnapshot(
            id,
            fifaMatch?.toDomain()!!,
            value!!,
            trendScopeAnalysis.map { it.toDomain() }.toMutableSet(),
            status,
            homeProfit,
            drawProfit,
            awayProfit,
            overProfit,
            underProfit,
            matchOddsWinnerSubType,
            goalLineWinnerSubType,
            createdAt
        )
    }
}

fun FifaOddSnapshot.toModel(): FifaOddSnapshotModel {
    return FifaOddSnapshotModel(
        this.id,
        this.fifaMatch.toModel(),
        this.value,
        this.trendScopeAnalysis.map { it.toModel() }.toMutableSet(),
        this.status,
        this.homeProfit,
        this.drawProfit,
        this.awayProfit,
        this.overProfit,
        this.underProfit,
        this.matchOddsWinnerSubType,
        this.goalLineWinnerSubType,
        this.createdAt
    )
}
