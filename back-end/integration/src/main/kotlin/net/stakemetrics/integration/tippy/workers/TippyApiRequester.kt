package net.stakemetrics.integration.tippy.workers

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.stakemetrics.application.entities.FifaLeague
import net.stakemetrics.application.entities.enums.FifaMarketTypes
import net.stakemetrics.application.entities.exceptions.IntegrationException
import net.stakemetrics.integration.tippy.deserializer.TippyDeserializer
import net.stakemetrics.integration.tippy.dto.TippyDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TippyApiRequester(private val tippyHelper: TippyHelper, private val deserializer: TippyDeserializer) {

    @Value("\${tippy.bet.token}")
    private val token: String? = null

    private val bet365TippyAPI: String = "bet365-api.tippy.club"
    private val client = OkHttpClient()

    fun getMatchesForLeagueAndMarket(fifaLeague: FifaLeague, market: FifaMarketTypes): TippyDTO.Response {
        val tippyLeague = tippyHelper.getTippyLeagueFromStakeMetricsLeague(fifaLeague)
        val tippyMarket = tippyHelper.getTippyMarketFromStakeMetricsMarket(market)

        val endpoint = "/v2/sport-categories/${tippyLeague.integrationId}?market_name=${tippyMarket.integrationName}"
        val jsonResponse = fetchBet365TippyApi(endpoint)
        return deserializer.parseJsonToResponse(jsonResponse)
    }

    fun autoBet(payload: TippyDTO.AutoBetRequest) {
        // TODO: Implement autoBet - Temporary Webhook call
        val webhookEndpoint = "https://webhook.site/cefc83a2-544a-45b1-9c17-4306df8b6ee7"

        val gson = Gson()
        val jsonPayload = gson.toJson(payload)

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonPayload.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(webhookEndpoint)
            .post(requestBody)
            .build()

        client.newCall(request).execute()
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