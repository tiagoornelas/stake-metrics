package net.stakemetrics.application.entities.exceptions

class EntityDoesntBelongToUserException() :
    RuntimeException("Entity does not belong to logged user.")