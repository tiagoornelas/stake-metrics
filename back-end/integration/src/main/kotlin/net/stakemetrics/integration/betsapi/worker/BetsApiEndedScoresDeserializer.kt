import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import net.stakemetrics.integration.betsapi.entities.dtos.BetsApiDTO

class BetsApiEndedScoresDeserializer : JsonDeserializer<BetsApiDTO.EndedScoresResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BetsApiDTO.EndedScoresResponse {
        val jsonObject = json.asJsonObject

        val resultsArray = jsonObject.getAsJsonArray("results")
        val modifiedResultsArray = JsonArray()

        resultsArray.forEach { resultElement ->
            val resultObject = resultElement.asJsonObject
            val timeStatusOrdinal = resultObject.get("time_status").asInt
            val timeStatus = BetsApiSoccerMatchStatus.entries[timeStatusOrdinal]
            resultObject.addProperty("time_status", timeStatus.name)
            modifiedResultsArray.add(resultObject)
        }

        jsonObject.add("results", modifiedResultsArray)

        return Gson().fromJson(jsonObject, BetsApiDTO.EndedScoresResponse::class.java)
    }
}