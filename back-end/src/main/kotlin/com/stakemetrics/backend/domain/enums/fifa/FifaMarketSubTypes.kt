package com.stakemetrics.backend.domain.enums.fifa

enum class FifaMarketSubTypes(val parentType: FifaMarketTypes) {
    HOME(FifaMarketTypes.MATCH_ODDS),
    DRAW(FifaMarketTypes.MATCH_ODDS),
    AWAY(FifaMarketTypes.MATCH_ODDS),
    OVER(FifaMarketTypes.GOAL_LINE),
    UNDER(FifaMarketTypes.GOAL_LINE)
}