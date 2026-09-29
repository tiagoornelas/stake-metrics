package net.stakemetrics.integration.autobettor

import net.stakemetrics.application.entities.AutoBettor
import net.stakemetrics.application.entities.FifaBet
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.enums.FifaMarketBetCandidates
import net.stakemetrics.application.utils.Logger
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class GenericAutoBettorServiceTest {

    private val logger = Logger()
    private val service = GenericAutoBettorService(logger)

    @Test
    fun `checkIntegration should return false when integrationId is null or blank`() {
        val autoBettor = AutoBettor(id = UUID.randomUUID(), integrationId = null)
        val result = service.checkIntegration(autoBettor)
        assertFalse(result.success)
    }

    @Test
    fun `checkIntegration should return true when integrationId is provided`() {
        val autoBettor = AutoBettor(id = UUID.randomUUID(), integrationId = "token-123", name = "Test Channel")
        val result = service.checkIntegration(autoBettor)
        assertTrue(result.success)
    }

    @Test
    fun `bet executes without error when webhook url is empty`() {
        val autoBettor = AutoBettor(id = UUID.randomUUID(), integrationId = "token-123")
        val bet = FifaBet(
            id = UUID.randomUUID(),
            line = FifaMarketBetCandidates.HOME,
            odds = 1.95,
            oddSnapshotId = UUID.randomUUID()
        )
        val request = FifaBetDTO.AutoBetRequest(fifaBet = bet, autoBettor = autoBettor)
        service.bet(request)
    }
}
