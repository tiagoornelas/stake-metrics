package com.stakemetrics.backend.plugins.http.controllers

import com.stakemetrics.backend.domain.exceptions.*
import com.stripe.exception.AuthenticationException
import com.stripe.exception.InvalidRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerController {

    @ExceptionHandler(InvalidFieldException::class)
    fun handleInvalidFieldException(e: InvalidFieldException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Invalid field")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(AlreadyExistsException::class)
    fun handleAlreadyExistsException(e: AlreadyExistsException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Already exists")
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Not found")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Authentication failed")
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(AccountRecoveryException::class)
    fun handleAccountRecoveryException(e: AccountRecoveryException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Account recovery failed")
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(PasswordConfirmationException::class)
    fun handlePasswordConfirmationException(e: PasswordConfirmationException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Password confirmation failed")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(EntityDoesntBelongToUserException::class)
    fun handleEntityDoesntBelongToUserException(e: EntityDoesntBelongToUserException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Entity doesn't belong to user")
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleInvalidStripeKeysException(e: AuthenticationException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(success = false, message = "Invalid Stripe keys. Check your integration.")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(InvalidRequestException::class)
    fun handleStripeInvalidRequestException(e: InvalidRequestException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Invalid request")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(AlreadyIntegratedException::class)
    fun handleAlreadyIntegratedException(e: AlreadyIntegratedException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Already integrated")
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    data class ErrorResponse(
        val success: Boolean, val message: String
    )
}