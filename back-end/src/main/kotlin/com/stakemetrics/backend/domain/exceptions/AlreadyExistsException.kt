package com.stakemetrics.backend.domain.exceptions

class AlreadyExistsException(entityName: String, key: String) :
    RuntimeException("$entityName with key '$key' already exists.")