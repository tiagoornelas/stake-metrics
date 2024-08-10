package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.MessengerDTO
import net.stakemetrics.application.entities.dtos.toChatDetailsResponse
import net.stakemetrics.application.entities.dtos.toMessengerChatResponse
import net.stakemetrics.application.service.IMessengerService
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/messenger")
class MessengerController(private val messengerService: IMessengerService) {

    @GetMapping("/{userId}")
    fun getUserTelegramIntegrations(@PathVariable userId: UUID): ResponseEntity<MessengerDTO.ListResponse> {
        val telegramIntegrations = messengerService.listIntegrationsForUser(userId)
        return ResponseEntity.ok(MessengerDTO.ListResponse(telegramIntegrations.map { it.toMessengerChatResponse() }))
    }

    @PostMapping("/private-chat/begin-integration/{userId}")
    fun beginIntegration(@PathVariable userId: UUID): ResponseEntity<MessengerDTO.BeginIntegrationResponse> {
        val integrationId = messengerService.beginPrivateChatIntegration(userId)
        return ResponseEntity.ok(MessengerDTO.BeginIntegrationResponse(integrationId))
    }

    @PostMapping("/private-chat/integrate/{userId}")
    fun integrate(@PathVariable userId: UUID): ResponseEntity<MessengerDTO.IntegrateResponse> {
        val chatDetails = messengerService.integratePrivateChat(userId)
        return ResponseEntity.ok(MessengerDTO.IntegrateResponse(chatDetails.toChatDetailsResponse()))
    }

    @PostMapping("/test-message/{telegramChatId}")
    fun sendTestMessage(@PathVariable telegramChatId: UUID): ResponseEntity<MessengerDTO.SendTestMessageResponse> {
        messengerService.sendTestMessage(telegramChatId)
        return ResponseEntity.ok(MessengerDTO.SendTestMessageResponse())
    }

    @PostMapping("/channel/integrate")
    fun integrateChannel(@RequestBody request: MessengerDTO.IntegrateChannelRequest): ResponseEntity<MessengerDTO
    .IntegrateChannelResponse> {
        messengerService.integrateChannel(request.userId, request.channelId)
        return ResponseEntity.ok(MessengerDTO.IntegrateChannelResponse())
    }

    @PutMapping
    fun editIntegrationSettings(@RequestBody request: MessengerDTO.EditIntegrationRequest): ResponseEntity<MessengerDTO
    .EditIntegrationResponse> {
        messengerService.editIntegrationSettings(request)
        return ResponseEntity.ok(MessengerDTO.EditIntegrationResponse())
    }

    @DeleteMapping("/{telegramChatId}")
    fun deleteIntegration(@PathVariable telegramChatId: UUID): ResponseEntity<MessengerDTO.DeleteIntegrationResponse> {
        messengerService.deleteIntegration(telegramChatId)
        return ResponseEntity.ok(MessengerDTO.DeleteIntegrationResponse())
    }
}