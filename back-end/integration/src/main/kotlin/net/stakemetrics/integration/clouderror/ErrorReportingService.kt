package net.stakemetrics.integration.clouderror

import com.google.devtools.clouderrorreporting.v1beta1.ProjectName
import com.google.devtools.clouderrorreporting.v1beta1.ReportErrorsServiceClient
import com.google.devtools.clouderrorreporting.v1beta1.ReportedErrorEvent
import com.google.devtools.clouderrorreporting.v1beta1.ServiceContext
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import net.stakemetrics.application.service.IErrorReportingService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ErrorReportingService : IErrorReportingService {

    @Value("\${app.gcp.project.id}")
    private val projectId: String = "stakemetrics"

    @Value("\${spring.profiles.active}")
    private val environment: String = "dev"

    override fun reportError(exception: Exception) {
        try {
            ReportErrorsServiceClient.create().use { serviceClient ->
                val sw = StringWriter()
                exception.printStackTrace(PrintWriter(sw))

                val serviceContext = ServiceContext.newBuilder()
                    .setService(environment)
                    .build()

                val errorEvent = ReportedErrorEvent.newBuilder()
                    .setMessage(sw.toString())
                    .setServiceContext(serviceContext)
                    .build()

                serviceClient.reportErrorEvent(ProjectName.of(projectId), errorEvent)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
