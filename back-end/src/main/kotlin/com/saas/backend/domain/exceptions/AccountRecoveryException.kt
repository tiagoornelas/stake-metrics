package com.stakemetrics.backend.domain.exceptions

class AccountRecoveryException() :
    RuntimeException("It was not possible to recover the account with given code.")