package net.stakemetrics.integration.tippy.enums

import net.stakemetrics.application.entities.enums.FifaMarketTypes

enum class TippyMarketsTypes(val applicationType: FifaMarketTypes, val integrationName: String) {
    MATCH_ODDS(FifaMarketTypes.MATCH_ODDS, "full_time_result"),
    ASIAN_GOAL_LINE(FifaMarketTypes.ASIAN_GOAL_LINE, "goal_line")
}