package net.stakemetrics.integration.tippy.deserializer

import com.google.gson.*
import net.stakemetrics.integration.tippy.dto.TippyDTO
import org.springframework.stereotype.Component
import java.lang.reflect.Type

@Component
class TippyEventsDeserializer : JsonDeserializer<TippyDTO.Response> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): TippyDTO.Response {
        val jsonObject = json.asJsonObject

        val eventsArray = jsonObject.getAsJsonArray("events")
        val modifiedEventsArray = JsonArray()

        eventsArray.forEach { eventElement ->
            val eventObject = eventElement.asJsonObject
            modifiedEventsArray.add(eventObject)
        }

        jsonObject.add("events", modifiedEventsArray)

        return Gson().fromJson(jsonObject, TippyDTO.Response::class.java)
    }

    fun parseJsonToResponse(response: JsonObject): TippyDTO.Response {
        val gson = GsonBuilder()
            .registerTypeAdapter(TippyDTO.Response::class.java, TippyEventsDeserializer())
            .create()
        return gson.fromJson(response, TippyDTO.Response::class.java)
    }
}