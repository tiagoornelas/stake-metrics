package com.stakemetrics.backend.plugins.http.controllers

import com.stakemetrics.backend.plugins.http.dto.TelegramDTO
import com.stakemetrics.backend.plugins.http.dto.toChatDetailsResponse
import com.stakemetrics.backend.plugins.http.dto.toTelegramChatResponse
import com.stakemetrics.backend.plugins.http.ports.TelegramServicePort
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/telegram")
class TelegramController(private val telegramServicePort: TelegramServicePort) {

    @GetMapping("/{userId}")
    fun getUserTelegramIntegrations(@PathVariable userId: UUID): ResponseEntity<TelegramDTO.ListResponse> {
        val telegramIntegrations = telegramServicePort.listIntegrationsForUser(userId)
        return ResponseEntity.ok(TelegramDTO.ListResponse(telegramIntegrations.map { it.toTelegramChatResponse() }))
    }

    @PostMapping("/private-chat/begin-integration/{userId}")
    fun beginIntegration(@PathVariable userId: UUID): ResponseEntity<TelegramDTO.BeginIntegrationResponse> {
        val integrationId = telegramServicePort.beginPrivateChatIntegration(userId)
        return ResponseEntity.ok(TelegramDTO.BeginIntegrationResponse(integrationId))
    }

    @PostMapping("/private-chat/integrate/{userId}")
    fun integrate(@PathVariable userId: UUID): ResponseEntity<TelegramDTO.IntegrateResponse> {
        val chatDetails = telegramServicePort.integratePrivateChat(userId)
        return ResponseEntity.ok(TelegramDTO.IntegrateResponse(chatDetails.toChatDetailsResponse()))
    }

    @PostMapping("/test-message/{telegramChatId}")
    fun sendTestMessage(@PathVariable telegramChatId: UUID): ResponseEntity<TelegramDTO.SendTestMessageResponse> {
        telegramServicePort.sendTestMessage(telegramChatId)
        return ResponseEntity.ok(TelegramDTO.SendTestMessageResponse())
    }

    @PostMapping("/channel/integrate")
    fun integrateChannel(@RequestBody request: TelegramDTO.IntegrateChannelRequest): ResponseEntity<TelegramDTO.IntegrateChannelResponse> {
        telegramServicePort.integrateChannel(request.userId, request.channelId)
        return ResponseEntity.ok(TelegramDTO.IntegrateChannelResponse())
    }

    @PutMapping
    fun editIntegrationSettings(@RequestBody request: TelegramDTO.EditIntegrationRequest): ResponseEntity<TelegramDTO.EditIntegrationResponse> {
        telegramServicePort.editIntegrationSettings(request)
        return ResponseEntity.ok(TelegramDTO.EditIntegrationResponse())
    }

    @DeleteMapping("/{telegramChatId}")
    fun deleteIntegration(@PathVariable telegramChatId: UUID): ResponseEntity<TelegramDTO.DeleteIntegrationResponse> {
        telegramServicePort.deleteIntegration(telegramChatId)
        return ResponseEntity.ok(TelegramDTO.DeleteIntegrationResponse())
    }
}