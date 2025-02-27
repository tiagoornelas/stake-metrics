package net.stakemetrics.application.entities.enums

enum class FifaMarketTypes(val subTypes: List<FifaMarketSubTypes>) {
    MATCH_ODDS(listOf(FifaMarketSubTypes.WINNER, FifaMarketSubTypes.DRAW)),
    ASIAN_GOAL_LINE(listOf(FifaMarketSubTypes.ASIAN_OVER_GOALS, FifaMarketSubTypes.ASIAN_UNDER_GOALS))
}

enum class FifaMarketSubTypes(val betCandidates: List<FifaMarketBetCandidates>) {
    WINNER(listOf(FifaMarketBetCandidates.HOME, FifaMarketBetCandidates.AWAY)),
    DRAW(listOf(FifaMarketBetCandidates.DRAW)),
    ASIAN_OVER_GOALS(listOf(FifaMarketBetCandidates.ASIAN_OVER_GOALS)),
    ASIAN_UNDER_GOALS(listOf(FifaMarketBetCandidates.ASIAN_UNDER_GOALS))
}

enum class FifaMarketBetCandidates {
    HOME,
    DRAW,
    AWAY,
    ASIAN_OVER_GOALS,
    ASIAN_UNDER_GOALS
}