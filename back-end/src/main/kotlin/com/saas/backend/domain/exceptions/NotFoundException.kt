package com.stakemetrics.backend.domain.exceptions

class NotFoundException(entityName: String, property: String, value: String) :
    RuntimeException("$entityName with $property '$value' not found.")