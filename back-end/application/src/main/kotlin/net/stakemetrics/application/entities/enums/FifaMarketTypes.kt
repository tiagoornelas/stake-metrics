package net.stakemetrics.application.entities.enums

enum class FifaMarketTypes(val subTypes: List<FifaMarketSubTypes>) {
    MATCH_ODDS(listOf(FifaMarketSubTypes.WINNER, FifaMarketSubTypes.DRAW)),
    GOAL_LINE(listOf(FifaMarketSubTypes.OVER, FifaMarketSubTypes.UNDER))
}

enum class FifaMarketSubTypes(val betCandidates: List<FifaMarketBetCandidates>) {
    WINNER(listOf(FifaMarketBetCandidates.HOME, FifaMarketBetCandidates.AWAY)),
    DRAW(listOf(FifaMarketBetCandidates.DRAW)),
    OVER(listOf(FifaMarketBetCandidates.OVER)),
    UNDER(listOf(FifaMarketBetCandidates.UNDER))
}

enum class FifaMarketBetCandidates {
    HOME,
    DRAW,
    AWAY,
    OVER,
    UNDER
}