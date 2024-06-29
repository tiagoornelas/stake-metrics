package com.saas.backend.domain.exceptions

class EntityDoesntBelongToUserException() :
    RuntimeException("Entity does not belong to logged user.")