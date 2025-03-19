package net.stakemetrics.integration.tippy.deserializer

import com.google.gson.*
import net.stakemetrics.integration.tippy.dto.TippyDTO
import org.springframework.stereotype.Component
import java.lang.reflect.Type

@Component
class TippyIntegrationDeserializer : JsonDeserializer<TippyDTO.IntegrationResponse> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): TippyDTO.IntegrationResponse {
        val jsonObject = json.asJsonObject
        return Gson().fromJson(jsonObject, TippyDTO.IntegrationResponse::class.java)
    }

    fun parseJsonToIntegrationResponse(response: JsonObject): TippyDTO.IntegrationResponse {
        val gson = GsonBuilder()
            .registerTypeAdapter(TippyDTO.IntegrationResponse::class.java, TippyIntegrationDeserializer())
            .create()
        return gson.fromJson(response, TippyDTO.IntegrationResponse::class.java)
    }
}