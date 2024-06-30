package com.stakemetrics.backend.plugins.email

import com.mailersend.sdk.MailerSend
import com.mailersend.sdk.emails.Email
import com.stakemetrics.backend.domain.ports.EmailSenderPort

class EmailSender(private val mailerSendApiKey: String, private val domainEmail: String) : EmailSenderPort {

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
