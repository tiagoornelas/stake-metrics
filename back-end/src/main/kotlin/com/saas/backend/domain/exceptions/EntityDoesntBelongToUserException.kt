package com.stakemetrics.backend.domain.exceptions

class EntityDoesntBelongToUserException() :
    RuntimeException("Entity does not belong to logged user.")