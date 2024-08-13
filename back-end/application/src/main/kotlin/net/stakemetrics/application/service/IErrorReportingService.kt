package net.stakemetrics.application.service

interface IErrorReportingService {
    fun reportError(exception: Exception)
}