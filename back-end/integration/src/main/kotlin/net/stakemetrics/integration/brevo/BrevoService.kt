package net.stakemetrics.integration.brevo

import net.stakemetrics.application.service.IEmailService
import net.stakemetrics.application.utils.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import sendinblue.ApiClient
import sendinblue.ApiException
import sibApi.TransactionalEmailsApi
import sibModel.SendSmtpEmail
import sibModel.SendSmtpEmailReplyTo
import sibModel.SendSmtpEmailSender
import sibModel.SendSmtpEmailTo

@Service
class BrevoService(private val logger: Logger) : IEmailService {

    @Value("\${brevo.api.key}")
    private val brevoApiKey: String = ""
    private val stakeMetricsEmail = "stakemetrics@gmail.com"

    override fun sendRecoveryCodeEmail(username: String, userEmail: String, code: String) {
        val defaultClient = ApiClient()
        defaultClient.setApiKey(brevoApiKey)

        val apiInstance = TransactionalEmailsApi(defaultClient)
        val sendSmtpEmail = SendSmtpEmail()

        sendSmtpEmail.subject = "Seu código de recuperação do Stake Metrics"
        sendSmtpEmail.to = listOf(SendSmtpEmailTo().apply { email = userEmail; name = username })
        sendSmtpEmail.sender = SendSmtpEmailSender().apply { name = "Stake Metrics"; email = stakeMetricsEmail }
        sendSmtpEmail.replyTo = SendSmtpEmailReplyTo().apply { name = "Stake Metrics"; email = stakeMetricsEmail }
        sendSmtpEmail.params = mapOf("code" to code, "name" to username)

        sendSmtpEmail.htmlContent = """
            <html>
            <body>
                <header>
                    <h2>Recuperação de Conta</h2>
                </header>
                <p>Olá,</p>
                <p>Você solicitou a recuperação de sua conta. Use o código abaixo para continuar o processo:</p>
                <h1>$code</h1>
                <footer>
                    <p>Equipe Stake Metrics</p>
                </footer>
            </body>
            </html>
        """.trimIndent()

        try {
            apiInstance.sendTransacEmail(sendSmtpEmail)
        } catch (e: ApiException) {
            logger.logError(e)
        }
    }
}