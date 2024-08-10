package net.stakemetrics.application.entities.exceptions

class PasswordDoesNotMatchException() :
    RuntimeException("Password does not match for specific account.")