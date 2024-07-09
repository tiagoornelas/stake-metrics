package com.stakemetrics.backend.plugins.http.controllers

import com.stakemetrics.backend.plugins.http.dto.UserDTO
import com.stakemetrics.backend.plugins.http.dto.toSubscriptionResponse
import com.stakemetrics.backend.plugins.http.dto.toUserResponse
import com.stakemetrics.backend.plugins.http.ports.SubscriptionServicePort
import com.stakemetrics.backend.plugins.http.ports.UserServicePort
import java.security.Principal
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserRestController(
    private val userServicePort: UserServicePort, private val subscriptionServicePort: SubscriptionServicePort
) {

    @PostMapping
    fun createUser(@RequestBody request: UserDTO.CreateRequest): ResponseEntity<UserDTO.CreateResponse> {
        userServicePort.create(
            request.name, request.email, request.password, request.passwordConfirmation
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDTO.CreateResponse(request.email))
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): ResponseEntity<UserDTO.FindResponse> {
        val user = userServicePort.findById(userId)
        val subscription = subscriptionServicePort.findByUser(user)
        val features = subscriptionServicePort.listUserFeatures(user)
        return ResponseEntity.status(HttpStatus.OK).body(
            UserDTO.FindResponse(user.toUserResponse(subscription.toSubscriptionResponse(features)))
        )
    }

    @PutMapping("/{userId}")
    fun editUser(
        @PathVariable userId: UUID, @RequestBody request: UserDTO.EditRequest, principal: Principal
    ): ResponseEntity<UserDTO.EditResponse> {
        userServicePort.edit(principal.name, userId, request.name, request.email)
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.EditResponse())
    }

    @PutMapping("/password/{userId}")
    fun editUser(
        @PathVariable userId: UUID,
        @RequestBody request: UserDTO.ChangePasswordRequest,
    ): ResponseEntity<UserDTO.ChangePasswordResponse> {
        userServicePort.changePassword(userId, request.currentPassword, request.password, request.passwordConfirmation)
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.ChangePasswordResponse())
    }
}