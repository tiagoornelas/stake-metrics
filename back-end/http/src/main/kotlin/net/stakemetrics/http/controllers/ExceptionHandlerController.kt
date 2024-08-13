package net.stakemetrics.http.controllers

import net.stakemetrics.application.entities.exceptions.*
import net.stakemetrics.application.service.IErrorReportingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerController(private val errorReportingService: IErrorReportingService) {

    @ExceptionHandler(InvalidFieldException::class)
    fun handleInvalidFieldException(e: InvalidFieldException): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Invalid field")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(AlreadyExistsException::class)
    fun handleAlreadyExistsException(e: AlreadyExistsException): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Already exists")
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Not found")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(PasswordDoesNotMatchException::class)
    fun handleBadCredentialsException(e: PasswordDoesNotMatchException): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Authentication failed")
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(AccountRecoveryException::class)
    fun handleAccountRecoveryException(e: AccountRecoveryException): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Account recovery failed")
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(PasswordConfirmationException::class)
    fun handlePasswordConfirmationException(e: PasswordConfirmationException): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Password confirmation failed")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(EntityDoesntBelongToUserException::class)
    fun handleEntityDoesntBelongToUserException(e: EntityDoesntBelongToUserException): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Entity doesn't belong to user")
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(AlreadyIntegratedException::class)
    fun handleAlreadyIntegratedException(e: AlreadyIntegratedException): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Already integrated")
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Illegal argument")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(NotAllowedException::class)
    fun handleNotAllowedException(e: NotAllowedException): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Not allowed")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ErrorResponse> {
        errorReportingService.reportError(e)
        val errorResponse = ErrorResponse(success = false, message = e.message ?: "Internal server error")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    data class ErrorResponse(
        val success: Boolean, val message: String
    )
}