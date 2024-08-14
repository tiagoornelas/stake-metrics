package net.stakemetrics.persistence.models

import jakarta.persistence.*
import java.util.Date
import java.util.UUID
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.entities.enums.FifaMarketTypes

@Entity
@Table(name = "fifa_bets")
data class FifaBetModel(
    @Id
    val id: UUID = UUID.randomUUID(),
    val isPaperBet: Boolean = false,
    @ManyToOne
    val strategy: FifaStrategyModel? = null,
    @ManyToOne
    @JoinColumn(name = "match_id")
    val match: FifaMatchModel? = null,
    val marketType: FifaMarketTypes? = null,
    val line: FifaMarketBetCandidates? = null,
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "bet_id")
    val scopes: MutableSet<FifaBetScopeModel> = mutableSetOf(),
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "bet_id")
    val messages: MutableSet<MessageModel> = mutableSetOf(),
    val handicap: Double? = null,
    val odds: Double = 0.0,
    val status: BetStatusTypes = BetStatusTypes.PENDING,
    val profit: Double? = null,
    val oddOfferTime: Date = Date(),
    val betTime: Date = Date()
) {
    fun toDomain(): FifaBet {
        return FifaBet(
            id,
            isPaperBet,
            strategy?.toDomain(),
            match?.toDomain(),
            marketType!!,
            line!!,
            scopes.map { it.toDomain() }.toMutableSet(),
            messages.map { it.toDomain() },
            handicap,
            odds,
            status,
            profit,
            oddOfferTime,
            betTime
        )
    }
}

fun FifaBet.toModel(): FifaBetModel {
    return FifaBetModel(
        id,
        isPaperBet,
        strategy?.toModel(),
        match?.toModel(),
        marketType,
        line,
        scopes.map { it.toModel() }.toMutableSet(),
        messages.map { it.toModel() }.toMutableSet(),
        handicap,
        odds,
        status,
        profit,
        oddOfferTime,
        betTime
    )
}