package net.stakemetrics.integration.betsapi.worker

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.stakemetrics.application.entities.exceptions.IntegrationException
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class BetsApiRequester {

    @Value("\${bets.api.token}")
    private val token: String? = null

    private val betsApiBaseUrl: String = "api.b365api.com"
    private val client = OkHttpClient()

    fun fetchEndedSoccerEvents(leagueId: Int, date: String, page: Int): JsonObject {
        val endpoint = "/v3/events/ended?sport_id=1&league_id=$leagueId&day=$date&page=$page"
        return fetchBetsApi(endpoint)
    }

    fun fetchUpcomingSoccerEvents(leagueId: Int, date: String, page: Int): JsonObject {
        val endpoint = "/v3/events/upcoming?sport_id=1&league_id=$leagueId&day=$date&page=$page"
        return fetchBetsApi(endpoint)
    }

    private fun fetchBetsApi(endpoint: String): JsonObject {
        val url = "https://$betsApiBaseUrl$endpoint&token=$token"
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    return JsonParser.parseString(responseBody).asJsonObject
                } else {
                    throw IntegrationException("Empty response body from BetsApi")
                }
            }
        } catch (e: Exception) {
            throw IntegrationException("Failed to fetch BetsApi: ${e.message}")
        }
    }
}