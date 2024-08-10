package net.stakemetrics.integration.betsapi.entities.enums

import net.stakemetrics.application.entities.enums.FifaMarketTypes

enum class MarketType(val value: String, val applicationType: FifaMarketTypes? = null) {
    MATCH_ODDS("1_1", FifaMarketTypes.MATCH_ODDS),
    ASIAN_HANDICAP("1_2"),
    GOAL_LINE("1_3", FifaMarketTypes.GOAL_LINE),
    ASIAN_CORNERS("1_4"),
    HT_ASIAN_HANDICAP("1_5"),
    HT_GOAL_LINE("1_6"),
    HT_ASIAN_CORNERS("1_7"),
    HT_MATCH_ODDS("1_8")
}