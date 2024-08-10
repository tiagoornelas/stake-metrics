package net.stakemetrics.application.entities.exceptions

class InvalidFieldException(fieldName: String, value: String) :
    RuntimeException("Invalid value $value for field $fieldName.")
