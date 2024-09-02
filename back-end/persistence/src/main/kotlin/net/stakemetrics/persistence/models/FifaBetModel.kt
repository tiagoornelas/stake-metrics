package net.stakemetrics.persistence.models

import jakarta.persistence.*
import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates

@Entity
@Table(name = "fifa_bets")
data class FifaBetModel(
    @Id val id: UUID = UUID.randomUUID(),
    val isPaperBet: Boolean = false,
    @ManyToOne val strategy: FifaStrategyModel? = null,
    @ManyToOne @JoinColumn(name = "match_id") val match: FifaMatchModel? = null,
    val line: FifaMarketBetCandidates? = null,
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    ) @JoinColumn(name = "bet_id") val messages: MutableSet<MessageModel> = mutableSetOf(),
    val handicap: Double? = null,
    val odds: Double? = null,
    val status: BetStatusTypes = BetStatusTypes.PENDING,
    val profit: Double? = null,
    val betTime: Date = Date(),
    val oddSnapshotId: UUID? = null
) {
    fun toDomain(): FifaBet {
        return FifaBet(
            id,
            isPaperBet,
            strategy?.toDomain(),
            match?.toDomain(),
            line!!,
            messages.map { it.toDomain() }.toMutableSet(),
            handicap,
            odds!!,
            status,
            profit,
            betTime,
            oddSnapshotId!!
        )
    }
}

fun FifaBet.toModel(): FifaBetModel {
    return FifaBetModel(
        id,
        isPaperBet,
        strategy?.toModel(),
        match?.toModel(),
        line,
        messages.map { it.toModel() }.toMutableSet(),
        handicap,
        odds,
        status,
        profit,
        betTime,
        oddSnapshotId
    )
}