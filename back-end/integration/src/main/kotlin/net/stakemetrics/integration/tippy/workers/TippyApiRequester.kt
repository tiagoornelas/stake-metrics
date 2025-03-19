package net.stakemetrics.integration.tippy.workers

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.exceptions.IntegrationException
import net.stakemetrics.application.utils.Logger
import net.stakemetrics.integration.tippy.deserializer.TippyEventsDeserializer
import net.stakemetrics.integration.tippy.deserializer.TippyIntegrationDeserializer
import net.stakemetrics.integration.tippy.dto.TippyDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TippyApiRequester(
    private val logger: Logger,
    private val tippyHelper: TippyHelper,
    private val eventsDeserializer: TippyEventsDeserializer,
    private val integrationDeserializer: TippyIntegrationDeserializer
) {

    @Value("\${tippy.bet.token}")
    private val token: String? = null

    private val bet365TippyAPI: String = "bet365-api.tippy.club"
    private val tippyApi: String = "api.tippy.club"
    private val client = OkHttpClient()

    fun checkIntegration(integrationId: String): TippyDTO.IntegrationResponse {
        val endpoint = "https://$tippyApi/v1/check-channel-authorization"
        val request = Request.Builder()
            .url(endpoint)
            .addHeader("Authorization", "Bearer $integrationId")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val jsonObject = JsonParser.parseString(responseBody).asJsonObject
                    return integrationDeserializer.parseJsonToIntegrationResponse(jsonObject)
                } else {
                    throw IntegrationException("Empty response body from TippyAPI")
                }
            }
        } catch (e: Exception) {
            throw IntegrationException("Failed to fetch TippyAPI: ${e.message}")
        }
    }

    fun getMatchesForLeagueAndMarket(fifaLeague: FifaLeague, market: FifaMarketTypes): TippyDTO.Response {
        val tippyLeague = tippyHelper.getTippyLeagueFromStakeMetricsLeague(fifaLeague)
        val tippyMarket = tippyHelper.getTippyMarketFromStakeMetricsMarket(market)

        val endpoint = "/v2/sport-categories/${tippyLeague.integrationId}?market_name=${tippyMarket.integrationName}"
        val jsonResponse = fetchBet365TippyApi(endpoint)
        return eventsDeserializer.parseJsonToResponse(jsonResponse)
    }

    fun autoBet(payload: TippyDTO.AutoBetRequestWithIntegrationInfo) {
        val endpoint = "https://$tippyApi/v1/send-tip"
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

        val body = TippyDTO.AutoBetRequest(payload.selectionId)
        val jsonPayload = Gson().toJson(body)
        val requestBody = jsonPayload.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(endpoint)
            .post(requestBody)
            .addHeader("Authorization", "Bearer ${payload.integrationId}")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                logger.log("AutoBet request successful: ${response.body?.string()}")
            } else {
                throw IntegrationException("Failed to send AutoBet request: ${response.body?.string()}")
            }
        } catch (e: Exception) {
            logger.logError(e)
        }
    }

    private fun fetchBet365TippyApi(endpoint: String): JsonObject {
        val url = "https://$bet365TippyAPI$endpoint"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    return JsonParser.parseString(responseBody).asJsonObject
                } else {
                    throw IntegrationException("Empty response body from TippyAPI - Bet 365")
                }
            }
        } catch (e: Exception) {
            throw IntegrationException("Failed to fetch TippyAPI - Bet 365: ${e.message}")
        }
    }

}