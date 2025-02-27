package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.dtos.UserDTO
import net.stakemetrics.application.entities.dtos.toAutoBettorResponse
import net.stakemetrics.application.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auto-bettor")
class AutoBettorController(private val userService: UserService) {

    @PostMapping
    fun saveAutoBettor(@RequestBody request: UserDTO.SaveAutoBettorRequest): ResponseEntity<UserDTO.SaveAutoBettorResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        userService.saveAutoBettorForUser(userEmail, request.integrationId)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDTO.SaveAutoBettorResponse())
    }

    @GetMapping
    fun getAutoBettor(): ResponseEntity<UserDTO.AutoBettorResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        val autoBettor = userService.getUserAutoBettor(userEmail)
            ?: return ResponseEntity.status(HttpStatus.OK).body(UserDTO.AutoBettorResponse(null))
        return ResponseEntity.status(HttpStatus.OK).body(autoBettor.toAutoBettorResponse())
    }

    @DeleteMapping
    fun deleteAutoBettor(): ResponseEntity<UserDTO.DeleteAutoBettorResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        userService.deleteAutoBettorForUser(userEmail)
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.DeleteAutoBettorResponse())
    }

    @PutMapping("/status")
    fun changeAutoBettorStatus(@RequestBody request: UserDTO.ChangeAutoBettorStatusRequest): ResponseEntity<UserDTO.ChangeAutoBettorResponse> {
        val userEmail = SecurityContextHolder.getContext().authentication.principal as String
        userService.changeAutoBettorStatus(userEmail, request.status)
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.ChangeAutoBettorResponse())
    }

}