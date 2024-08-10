package net.stakemetrics.integration.cloudtask

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.cloud.tasks.v2.*
import com.google.protobuf.ByteString
import io.grpc.ManagedChannelBuilder
import java.nio.charset.StandardCharsets
import net.stakemetrics.application.entities.annotations.EnvironmentSensitive
import net.stakemetrics.application.entities.dtos.FifaDTO
import net.stakemetrics.application.service.IQueueService
import net.stakemetrics.application.utils.EnvironmentVerifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@EnvironmentSensitive
class CloudTaskClientService(
    private val objectMapper: ObjectMapper,
    private val environmentVerifier: EnvironmentVerifier
) : IQueueService {

    @Value("\${app.backend.queue.base.url}")
    private val baseUrl: String = ""

    @Value("\${app.gcp.project.id}")
    private val projectId: String = "stakemetrics"

    @Value("\${app.gcp.location.id}")
    private val locationId: String = "us-central1"

    override fun enqueueSaveMatchResultTask(match: FifaDTO.FifaMatchRequest) {
        val payload = objectMapper.writeValueAsString(match).toByteArray(StandardCharsets.UTF_8)

        createCloudTaskClient().use { client ->
            val queuePath = QueueName.of(projectId, locationId, "save-match-result").toString()
            val fullUrl = "$baseUrl/service/fifa/match"
            enqueueTask(fullUrl, payload, client, queuePath)
        }
    }

    override fun enqueueCheckOddForStrategyTask(payload: FifaDTO.FifaStrategyAgainstOddRequest) {
        val payload = objectMapper.writeValueAsString(payload).toByteArray(StandardCharsets.UTF_8)

        createCloudTaskClient().use { client ->
            val queuePath = QueueName.of(projectId, locationId, "run-strategy-against-odds").toString()
            val fullUrl = "$baseUrl/queue/fifa/strategy-against-odds"
            enqueueTask(fullUrl, payload, client, queuePath)
        }
    }

    override fun enqueueSendMessageTask(payload: Any) {
        val payload = objectMapper.writeValueAsString(payload).toByteArray(StandardCharsets.UTF_8)

        createCloudTaskClient().use { client ->
            val queuePath = QueueName.of(projectId, locationId, "send-message").toString()
            val fullUrl = "$baseUrl/telegram/send-message"
            enqueueTask(fullUrl, payload, client, queuePath)
        }
    }

    private fun createCloudTaskClient(): CloudTasksClient {
        return if (!environmentVerifier.isProd()) {
            val channel = ManagedChannelBuilder.forAddress("localhost", 8123)
                .usePlaintext()
                .build()
            val channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel))
            val settings = CloudTasksSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(NoCredentialsProvider.create())
                .build()
            CloudTasksClient.create(settings)
        } else {
            CloudTasksClient.create()
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