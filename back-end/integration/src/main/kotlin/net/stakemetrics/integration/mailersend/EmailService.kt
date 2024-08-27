package net.stakemetrics.integration.mailersend

import com.mailersend.sdk.MailerSend
import com.mailersend.sdk.emails.Email
import net.stakemetrics.application.service.IEmailService
import net.stakemetrics.application.utils.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EmailService(private val logger: Logger) : IEmailService {

    @Value("\${mailersend.api.key}")
    private val mailerSendApiKey: String = ""

    @Value("\${mailersend.domain.email}")
    private val domainEmail: String = "MS_ngBPn6@stakemetrics.net"

    override fun sendRecoveryCodeEmail(username: String, userEmail: String, code: String) {
        val email = Email()
        val ms = MailerSend()
        ms.token = mailerSendApiKey

        email.setFrom("Stake Metrics", domainEmail)
        email.addRecipient(username, userEmail)
        email.setTemplateId("jy7zpl90reol5vx6")
        email.addPersonalization("code", code)
        email.addPersonalization("name", username)
        email.setSubject("Seu código de recuperação do Stake Metrics")
        ms.emails().send(email)
    }

}
