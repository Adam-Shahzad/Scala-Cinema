package controllers

import javax.inject.Inject

import play.api.libs.mailer._

class MailerService @Inject()(mailerClient: MailerClient) {

  def sendEmail(subject: String, from: String, message: String) = {
    val email = Email(
      subject,
      from,
      Seq("fahri.ulucay@qa.com"),
      // adds attachment

      // sends text, HTML or both...
      bodyText = Some("who is who"),
      bodyHtml = Some(s"""<html><body><p>An <b>html</b> From: $from  $message </p></body></html>""")
    )
    mailerClient.send(email)
  }

}