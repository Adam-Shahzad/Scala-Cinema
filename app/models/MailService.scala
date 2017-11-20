package models

import javax.inject.Inject

import play.api.libs.mailer.{Email, MailerClient}

class MailService @Inject() (mailerClient:MailerClient) {

  def sendEmail(email_user: String, subject: String, msg: String): Unit = {
    val email = Email(
      subject,
      "FROM <"+email_user+">",
      Seq("TO <mariannepearson0@gmail.com>"),
      bodyText = Some(msg)
    )
    mailerClient.send(email)
  }
}
