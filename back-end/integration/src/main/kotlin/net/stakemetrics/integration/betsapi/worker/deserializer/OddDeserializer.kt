package net.stakemetrics.integration.betsapi.worker.deserializer

import com.google.gson.*
import java.lang.reflect.Type
import net.stakemetrics.integration.betsapi.entities.dtos.BetsApiDTO
import net.stakemetrics.integration.betsapi.entities.enums.MarketType
import org.springframework.stereotype.Component

@Component
class OddDeserializer : JsonDeserializer<BetsApiDTO.EventOddsResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BetsApiDTO.EventOddsResponse {
        val jsonObject = json.asJsonObject
        val resultsObject = jsonObject.getAsJsonObject("results")

        val oddsObject = resultsObject.getAsJsonObject("odds") ?: JsonObject()
        val filteredOdds = JsonObject()

        oddsObject.entrySet().forEach { entry ->
            val marketType = MarketType.entries.find { it.value == entry.key }
            if (marketType?.applicationType != null) {
                filteredOdds.add(marketType.name, entry.value)
            }
        }

        jsonObject.add("odds", filteredOdds)

        val statsObject = resultsObject.getAsJsonObject("stats") ?: JsonObject()
        val oddsUpdateObject = statsObject.getAsJsonObject("odds_update") ?: JsonObject()
        val filteredOddsUpdate = JsonObject()

        oddsUpdateObject.entrySet().forEach { entry ->
            val marketType = MarketType.entries.find { it.value == entry.key }
            if (marketType?.applicationType != null) {
                filteredOddsUpdate.add(marketType.name, entry.value)
            }
        }

        val filteredStatsObject = JsonObject()
        statsObject.entrySet().forEach { entry ->
            if (entry.key != "matching_dir" && entry.key != "odds_update") {
                filteredStatsObject.add(entry.key, entry.value)
            }
        }
        filteredStatsObject.add("odds_update", filteredOddsUpdate)

        jsonObject.add("stats", filteredStatsObject)

        return Gson().fromJson(jsonObject, BetsApiDTO.EventOddsResponse::class.java)
    }

    fun parseJsonToOddResponse(response: JsonObject): BetsApiDTO.EventOddsResponse {
        val gson = GsonBuilder()
            .registerTypeAdapter(BetsApiDTO.EventOddsResponse::class.java, OddDeserializer())
            .create()
        return gson.fromJson(response, BetsApiDTO.EventOddsResponse::class.java)
    }
}