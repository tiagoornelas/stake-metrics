package com.saas.backend.domain.exceptions

class PasswordConfirmationException() :
    RuntimeException("Password and password confirmation do not match.")