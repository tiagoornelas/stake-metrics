package net.stakemetrics.integration.betsapi.worker.deserializer

import com.google.gson.*
import java.lang.reflect.Type
import net.stakemetrics.integration.betsapi.entities.dtos.BetsApiDTO
import org.springframework.stereotype.Component

@Component
class MatchDeserializer : JsonDeserializer<BetsApiDTO.MatchResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BetsApiDTO.MatchResponse {
        val jsonObject = json.asJsonObject

        val resultsArray = jsonObject.getAsJsonArray("results")
        val modifiedResultsArray = JsonArray()

        resultsArray.forEach { resultElement ->
            val resultObject = resultElement.asJsonObject
            val timeStatusOrdinal = resultObject.get("time_status").asInt

            val timeStatus = when (timeStatusOrdinal) {
                99 -> BetsApiSoccerMatchStatus.REMOVED
                in 0 until BetsApiSoccerMatchStatus.entries.size -> BetsApiSoccerMatchStatus.entries[timeStatusOrdinal]
                else -> BetsApiSoccerMatchStatus.NOT_STARTED
            }
            
            resultObject.addProperty("time_status", timeStatus.name)
            modifiedResultsArray.add(resultObject)
        }

        jsonObject.add("results", modifiedResultsArray)

        return Gson().fromJson(jsonObject, BetsApiDTO.MatchResponse::class.java)
    }

    fun parseJsonToMatchResponse(response: JsonObject): BetsApiDTO.MatchResponse {
        val gson = GsonBuilder()
            .registerTypeAdapter(BetsApiDTO.MatchResponse::class.java, MatchDeserializer())
            .create()
        return gson.fromJson(response, BetsApiDTO.MatchResponse::class.java)
    }
}