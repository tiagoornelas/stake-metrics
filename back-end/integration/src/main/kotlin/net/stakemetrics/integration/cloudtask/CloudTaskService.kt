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
import net.stakemetrics.application.entities.dtos.FifaBetDTO
import net.stakemetrics.application.entities.dtos.FifaDataSourceDTO
import net.stakemetrics.application.entities.dtos.FifaStrategyDTO
import net.stakemetrics.application.entities.dtos.MessengerDTO
import net.stakemetrics.application.service.IQueueService
import net.stakemetrics.application.utils.EnvironmentVerifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@EnvironmentSensitive
class CloudTaskService(
    private val objectMapper: ObjectMapper,
    private val environmentVerifier: EnvironmentVerifier
) : IQueueService {

    @Value("\${app.backend.queue.base.url}")
    private val baseUrl: String = ""

    @Value("\${app.gcp.project.id}")
    private val projectId: String = "stakemetrics"

    @Value("\${app.gcp.location.id}")
    private val locationId: String = "us-central1"

    override fun enqueueSaveMatchResultTask(payload: FifaDataSourceDTO.FifaMatchRequest) {
        createCloudTaskClient().use { client ->
            val queueName = "save-match-result"
            val queuePath = QueueName.of(projectId, locationId, queueName).toString()
            val fullUrl = "$baseUrl/queue/fifa/$queueName"
            enqueueTask(fullUrl, getJsonPayload(payload), client, queuePath)
        }
    }

    override fun enqueueRunStrategyAgainstOddTask(payload: FifaStrategyDTO.FifaStrategyAgainstOddRequest) {
        createCloudTaskClient().use { client ->
            val queueName = "run-strategy-against-odds"
            val queuePath = QueueName.of(projectId, locationId, queueName).toString()
            val fullUrl = "$baseUrl/queue/fifa/$queueName"
            enqueueTask(fullUrl, getJsonPayload(payload), client, queuePath)
        }
    }

    override fun enqueueBetTask(payload: FifaBetDTO.BetRequest) {
        createCloudTaskClient().use { client ->
            val queueName = "bet-queue"
            val queuePath = QueueName.of(projectId, locationId, queueName).toString()
            val fullUrl = "$baseUrl/queue/fifa/$queueName"
            enqueueTask(fullUrl, getJsonPayload(payload), client, queuePath)
        }
    }

    override fun enqueueCloseBetTask(payload: FifaBetDTO.CloseBetRequest) {
        createCloudTaskClient().use { client ->
            val queueName = "close-bet"
            val queuePath = QueueName.of(projectId, locationId, queueName).toString()
            val fullUrl = "$baseUrl/queue/fifa/$queueName"
            enqueueTask(fullUrl, getJsonPayload(payload), client, queuePath)
        }
    }

    override fun enqueueMessageTask(payload: MessengerDTO.EnqueueRequest, delay: Int?) {
        createCloudTaskClient().use { client ->
            val queueName = "message-queue"
            val queuePath = QueueName.of(projectId, locationId, queueName).toString()
            val fullUrl = "$baseUrl/queue/fifa/$queueName"
            enqueueTask(fullUrl, getJsonPayload(payload), client, queuePath, delay)
        }
    }

    private fun getJsonPayload(payload: Any) =
        objectMapper.writeValueAsString(payload).toByteArray(StandardCharsets.UTF_8)

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
        queuePath: String,
        delay: Int? = null
    ): Task? {
        val httpRequest = HttpRequest.newBuilder()
            .putHeaders("Content-Type", "application/json")
            .setHttpMethod(HttpMethod.POST)
            .setUrl(fullUrl)
            .setBody(ByteString.copyFrom(payload))
            .build()

        val taskBuilder = Task.newBuilder()
            .setHttpRequest(httpRequest)

        if (delay != null) {
            val scheduleTime = com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(System.currentTimeMillis() / 1000 + delay)
                .build()
            taskBuilder.setScheduleTime(scheduleTime)
        }

        val task = taskBuilder.build()
        return client.createTask(queuePath, task)
    }
}