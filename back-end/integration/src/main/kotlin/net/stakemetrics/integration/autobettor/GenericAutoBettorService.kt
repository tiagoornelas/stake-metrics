package net.stakemetrics.integration.autobettor

import com.fasterxml.jackson.databind.ObjectMapper
import net.stakemetrics.application.entities.AutoBettor
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.service.IAutoBettorService
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.integration.autobettor.dto.AutoBettorWebhookPayload
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
class GenericAutoBettorService(
    private val logger: Logger
) : IAutoBettorService {

    @Value("\${autobettor.webhook.url:}")
    private val webhookUrl: String = ""

    private val objectMapper = ObjectMapper()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    override fun bet(payload: FifaBetDTO.AutoBetRequest) {
        val fifaBet = payload.fifaBet
        val autoBettor = payload.autoBettor

        val webhookPayload = AutoBettorWebhookPayload(
            betId = fifaBet.id,
            matchIntegrationId = fifaBet.match?.integrationId,
            homePlayer = fifaBet.match?.home?.name,
            awayPlayer = fifaBet.match?.away?.name,
            league = fifaBet.match?.league?.name,
            market = fifaBet.line.name,
            odds = fifaBet.odds,
            integrationId = autoBettor.integrationId,
            timestamp = Instant.now().toString()
        )

        if (webhookUrl.isBlank()) {
            return
        }

        try {
            val jsonBody = objectMapper.writeValueAsString(webhookPayload)
            val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
            val requestBuilder = Request.Builder()
                .url(webhookUrl)
                .post(requestBody)

            if (!autoBettor.integrationId.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer ${autoBettor.integrationId}")
            }

            httpClient.newCall(requestBuilder.build()).execute().use { response ->
                if (!response.isSuccessful) {
                    logger.logError(Exception("AutoBettor webhook responded with HTTP ${response.code}"))
                }
            }
        } catch (e: Exception) {
            logger.logError(e)
        }
    }

    override fun checkIntegration(autoBettor: AutoBettor): FifaBetDTO.AutoBettorIntegrationResponse {
        val token = autoBettor.integrationId
        if (token.isNullOrBlank()) {
            return FifaBetDTO.AutoBettorIntegrationResponse(
                success = false,
                channelName = null
            )
        }

        return FifaBetDTO.AutoBettorIntegrationResponse(
            success = true,
            channelName = autoBettor.name ?: "Canal de Apostas"
        )
    }
}
