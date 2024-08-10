package net.stakemetrics.application.service

import net.stakemetrics.application.entities.dtos.SubscriptionDTO
import org.springframework.stereotype.Service

@Service
interface ISubscriptionWebhookService {
    fun handle(event: SubscriptionDTO.SubscriptionEvent)
}