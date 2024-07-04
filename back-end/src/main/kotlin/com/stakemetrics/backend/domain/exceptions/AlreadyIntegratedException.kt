package com.stakemetrics.backend.domain.exceptions

class AlreadyIntegratedException() :
    RuntimeException("The integration already exists.")