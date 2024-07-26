package com.stakemetrics.backend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.tasks.v2.*
import com.google.protobuf.ByteString
import com.stakemetrics.backend.plugins.http.dto.FifaDTO
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class CloudTaskClientService(
    @Value("\${app.backend.base.url}") private val baseUrl: String,
    private val objectMapper: ObjectMapper
) {

    private val projectId: String = "stakemetrics"
    private val locationId: String = "us-central1"

    fun enqueueSaveMatchResultTask(match: FifaDTO.FifaMatchRequest, request: HttpServletRequest) {
        val payload = objectMapper.writeValueAsString(match).toByteArray(StandardCharsets.UTF_8)

        CloudTasksClient.create().use { client ->
            val queuePath = QueueName.of(projectId, locationId, "save-match-result").toString()
            val fullUrl = "$baseUrl/service/fifa/match"
            enqueueTask(request, fullUrl, payload, client, queuePath)
        }
    }

    fun enqueueCheckOddForStrategyTask(payload: FifaDTO.FifaStrategyAgainstOddRequest, request: HttpServletRequest) {
        val payload = objectMapper.writeValueAsString(payload).toByteArray(StandardCharsets.UTF_8)

        CloudTasksClient.create().use { client ->
            val queuePath = QueueName.of(projectId, locationId, "run-strategy-against-odds").toString()
            val fullUrl = "$baseUrl/service/fifa/strategy-against-odds"
            enqueueTask(request, fullUrl, payload, client, queuePath)
        }
    }

    fun enqueueSendTelegramMessageTask(pl: Any, request: HttpServletRequest) {
        val payload = objectMapper.writeValueAsString(pl).toByteArray(StandardCharsets.UTF_8)

        CloudTasksClient.create().use { client ->
            val queuePath = QueueName.of(projectId, locationId, "send-telegram-message").toString()
            val fullUrl = "$baseUrl/telegram/send-message"
            enqueueTask(request, fullUrl, payload, client, queuePath)
        }
    }

    private fun enqueueTask(
        request: HttpServletRequest,
        fullUrl: String,
        payload: ByteArray,
        client: CloudTasksClient,
        queuePath: String
    ): Task? {
        val httpRequest = HttpRequest.newBuilder()
            .putHeaders("Authorization", request.getHeader("Authorization"))
            .putHeaders("Content-Type", "application/json")
            .setHttpMethod(HttpMethod.POST)
            .setUrl(fullUrl)
            .setBody(ByteString.copyFrom(payload))
            .build()

        val task = Task.newBuilder()
            .setHttpRequest(httpRequest)
            .build()

        return client.createTask(queuePath, task)
    }
}