package net.stakemetrics.integration.mailersend

import com.mailersend.sdk.MailerSend
import com.mailersend.sdk.emails.Email
import net.stakemetrics.application.service.IEmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EmailService() : IEmailService {

    @Value("\${mailersend.api.key}")
    private val mailerSendApiKey: String = ""

    @Value("\${mailersend.domain.email}")
    private val domainEmail: String = ""

    override fun sendRecoveryCodeEmail(username: String, userEmail: String, code: String) {
        val email = Email()
        val ms = MailerSend()
        ms.token = mailerSendApiKey

        email.setFrom("Stake Metrics", domainEmail)
        email.addRecipient(username, userEmail)
        email.setSubject("Recuperação de Conta")
        email.setPlain("O seu código de recuperação de conta para o Stake Metrics é: $code")
        ms.emails().send(email)
    }

}
