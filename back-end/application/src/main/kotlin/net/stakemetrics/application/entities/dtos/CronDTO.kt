package net.stakemetrics.application.entities.dtos

class CronDTO {

    data class Response(
        val success: Boolean = true,
        val elapsedTimeInSeconds: Double
    )
}