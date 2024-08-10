package net.stakemetrics.application.entities.exceptions

class PasswordConfirmationException() :
    RuntimeException("Password and password confirmation do not match.")