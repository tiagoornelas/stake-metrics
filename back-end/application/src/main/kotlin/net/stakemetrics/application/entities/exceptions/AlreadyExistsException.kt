package net.stakemetrics.application.entities.exceptions

class AlreadyExistsException(entityName: String, key: String) :
    RuntimeException("$entityName with key '$key' already exists.")