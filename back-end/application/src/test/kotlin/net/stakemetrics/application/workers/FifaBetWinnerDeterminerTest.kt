package net.stakemetrics.application.workers

import net.stakemetrics.application.entities.FifaMatch
import net.stakemetrics.application.entities.enums.BetStatusTypes
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.Date
import java.util.stream.Stream

class FifaBetWinnerDeterminerTest {

    private val determiner = FifaBetWinnerDeterminer()

    companion object {
        @JvmStatic
        fun goalLineTestCases(): Stream<Arguments> = Stream.of(
            Arguments.of(4.5, 4, FifaMarketBetCandidates.UNDER, BetStatusTypes.WON),
            Arguments.of(4.5, 5, FifaMarketBetCandidates.UNDER, BetStatusTypes.LOST),
            Arguments.of(4.5, 4, FifaMarketBetCandidates.OVER, BetStatusTypes.LOST),
            Arguments.of(4.5, 5, FifaMarketBetCandidates.OVER, BetStatusTypes.WON),

            Arguments.of(4.0, 4, FifaMarketBetCandidates.UNDER, BetStatusTypes.VOID),
            Arguments.of(4.0, 3, FifaMarketBetCandidates.UNDER, BetStatusTypes.WON),
            Arguments.of(4.0, 5, FifaMarketBetCandidates.UNDER, BetStatusTypes.LOST),
            Arguments.of(4.0, 4, FifaMarketBetCandidates.OVER, BetStatusTypes.VOID),
            Arguments.of(4.0, 5, FifaMarketBetCandidates.OVER, BetStatusTypes.WON),
            Arguments.of(4.0, 3, FifaMarketBetCandidates.OVER, BetStatusTypes.LOST),

            Arguments.of(4.25, 4, FifaMarketBetCandidates.UNDER, BetStatusTypes.HALF_WON),
            Arguments.of(4.25, 5, FifaMarketBetCandidates.UNDER, BetStatusTypes.LOST),
            Arguments.of(4.25, 3, FifaMarketBetCandidates.UNDER, BetStatusTypes.WON),
            Arguments.of(4.25, 4, FifaMarketBetCandidates.OVER, BetStatusTypes.HALF_LOST),
            Arguments.of(4.25, 5, FifaMarketBetCandidates.OVER, BetStatusTypes.WON),
            Arguments.of(4.25, 3, FifaMarketBetCandidates.OVER, BetStatusTypes.LOST),

            Arguments.of(4.75, 5, FifaMarketBetCandidates.UNDER, BetStatusTypes.HALF_LOST),
            Arguments.of(4.75, 4, FifaMarketBetCandidates.UNDER, BetStatusTypes.WON),
            Arguments.of(4.75, 6, FifaMarketBetCandidates.UNDER, BetStatusTypes.LOST),
            Arguments.of(4.75, 5, FifaMarketBetCandidates.OVER, BetStatusTypes.HALF_WON),
            Arguments.of(4.75, 6, FifaMarketBetCandidates.OVER, BetStatusTypes.WON),
            Arguments.of(4.75, 4, FifaMarketBetCandidates.OVER, BetStatusTypes.LOST)
        )
    }

    @ParameterizedTest
    @MethodSource("goalLineTestCases")
    fun `should correctly determine goal line winner for all handicap cases`(
        handicap: Double,
        totalGoals: Int,
        candidate: FifaMarketBetCandidates,
        expectedStatus: BetStatusTypes
    ) {
        val match = FifaMatch(
            integrationId = 1L,
            time = Date(),
            totalGoalsAtFullTime = totalGoals
        )
        val result = determiner.determineGoalLineWinner(match, candidate, 2.0, handicap)
        assertEquals(expectedStatus, result.status)
    }

    @Test
    fun `should calculate correct profit for winning bet`() {
        val match = FifaMatch(
            integrationId = 1L,
            time = Date(),
            totalGoalsAtFullTime = 5
        )
        val result = determiner.determineGoalLineWinner(match, FifaMarketBetCandidates.OVER, 2.0, 4.5)
        assertEquals(1.0, result.profit)
    }

    @Test
    fun `should calculate correct profit for losing bet`() {
        val match = FifaMatch(
            integrationId = 1L,
            time = Date(),
            totalGoalsAtFullTime = 4
        )
        val result = determiner.determineGoalLineWinner(match, FifaMarketBetCandidates.OVER, 2.0, 4.5)
        assertEquals(-1.0, result.profit)
    }

    @Test
    fun `should calculate correct profit for half win bet`() {
        val match = FifaMatch(
            integrationId = 1L,
            time = Date(),
            totalGoalsAtFullTime = 4
        )
        val result = determiner.determineGoalLineWinner(match, FifaMarketBetCandidates.UNDER, 2.0, 4.25)
        assertEquals(0.5, result.profit)
    }

    @Test
    fun `should calculate correct profit for half loss bet`() {
        val match = FifaMatch(
            integrationId = 1L,
            time = Date(),
            totalGoalsAtFullTime = 4
        )
        val result = determiner.determineGoalLineWinner(match, FifaMarketBetCandidates.OVER, 2.0, 4.25)
        assertEquals(-0.5, result.profit)
    }

    @Test
    fun `should calculate correct profit for void bet`() {
        val match = FifaMatch(
            integrationId = 1L,
            time = Date(),
            totalGoalsAtFullTime = 4
        )
        val result = determiner.determineGoalLineWinner(match, FifaMarketBetCandidates.OVER, 2.0, 4.0)
        assertEquals(0.0, result.profit)
    }
}
