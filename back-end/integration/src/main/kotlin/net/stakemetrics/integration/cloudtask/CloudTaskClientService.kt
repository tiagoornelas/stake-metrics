package net.stakemetrics.integration.cloudtask

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.cloud.tasks.v2.CloudTasksClient
import com.google.cloud.tasks.v2.HttpMethod
import com.google.cloud.tasks.v2.HttpRequest
import com.google.cloud.tasks.v2.QueueName
import com.google.cloud.tasks.v2.Task
import com.google.protobuf.ByteString
import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.service.IQueueService
import java.nio.charset.StandardCharsets
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CloudTaskClientService(
    private val objectMapper: ObjectMapper
) : IQueueService {

    @Value("\${app.backend.base.url}")
    private val baseUrl: String = ""

    @Value("\${app.gcp.project.id}")
    private val projectId: String = "stakemetrics"

    @Value("\${app.gcp.location.id}")
    private val locationId: String = "us-central1"

    override fun enqueueSaveMatchResultTask(match: FifaDTO.FifaMatchRequest) {
        val payload = objectMapper.writeValueAsString(match).toByteArray(StandardCharsets.UTF_8)

        CloudTasksClient.create().use { client ->
            val queuePath = QueueName.of(projectId, locationId, "save-match-result").toString()
            val fullUrl = "$baseUrl/service/fifa/match"
            enqueueTask(fullUrl, payload, client, queuePath)
        }
    }

    override fun enqueueCheckOddForStrategyTask(payload: FifaDTO.FifaStrategyAgainstOddRequest) {
        val payload = objectMapper.writeValueAsString(payload).toByteArray(StandardCharsets.UTF_8)

        CloudTasksClient.create().use { client ->
            val queuePath = QueueName.of(projectId, locationId, "run-strategy-against-odds").toString()
            val fullUrl = "$baseUrl/service/fifa/strategy-against-odds"
            enqueueTask(fullUrl, payload, client, queuePath)
        }
    }

    override fun enqueueSendMessageTask(payload: Any) {
        val payload = objectMapper.writeValueAsString(payload).toByteArray(StandardCharsets.UTF_8)

        CloudTasksClient.create().use { client ->
            val queuePath = QueueName.of(projectId, locationId, "send-telegram-message").toString()
            val fullUrl = "$baseUrl/telegram/send-message"
            enqueueTask(fullUrl, payload, client, queuePath)
        }
    }

    private fun enqueueTask(
        fullUrl: String,
        payload: ByteArray,
        client: CloudTasksClient,
        queuePath: String
    ): Task? {
        val httpRequest = HttpRequest.newBuilder()
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