package net.stakemetrics.http.controllers

import java.security.Principal
import java.util.UUID
import net.stakemetrics.application.entities.dtos.UserDTO
import net.stakemetrics.application.entities.dtos.toUserResponse
import net.stakemetrics.application.service.ISubscriptionService
import net.stakemetrics.application.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService, private val subscriptionService: ISubscriptionService) {

    @PostMapping
    fun createUser(@RequestBody request: UserDTO.CreateRequest): ResponseEntity<UserDTO.CreateResponse> {
        userService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDTO.CreateResponse(request.email))
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): ResponseEntity<UserDTO.FindResponse> {
        val user = userService.findById(userId)
        val subscription = subscriptionService.getSubscriptionDetails(user)
        return ResponseEntity.status(HttpStatus.OK).body(
            UserDTO.FindResponse(user.toUserResponse(subscription))
        )
    }

    @PutMapping("/{userId}")
    fun editUser(
        @PathVariable userId: UUID, @RequestBody request: UserDTO.EditRequest, principal: Principal
    ): ResponseEntity<UserDTO.EditResponse> {
        userService.edit(principal.name, userId, request)
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.EditResponse())
    }

    @PutMapping("/password/{userId}")
    fun editUser(
        @PathVariable userId: UUID,
        @RequestBody request: UserDTO.ChangePasswordRequest,
    ): ResponseEntity<UserDTO.ChangePasswordResponse> {
        userService.changePassword(userId, request)
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.ChangePasswordResponse())
    }
}