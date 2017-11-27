package controllers

import javax.inject.Inject

import play.api.libs.mailer._

import scala.collection.mutable.ArrayBuffer

class MailerService @Inject()(mailerClient: MailerClient) {

  def sendEmail(subject: String, from: String, message: String, to: String = "scala.qacinemas@gmail.com") = {
    val email = Email(
      subject,
      from,
      Seq(to),
      // adds attachment

      // sends text, HTML or both...
      bodyText = Some("who is who"),
      bodyHtml = Some(s"""<html><body><p>An <b>html</b> From: $from  $message </p></body></html>""")
    )
    mailerClient.send(email)
  }

  def sendBookingConfirmation(booker: String, to: String , message: String) = {
    var messageFormatter = ArrayBuffer("<br />Movie Time","<br />Adult Ticket(s):","<br />Child Ticket(s):","<br />Concession Ticket(s):" )
    val messageSplitted = message.split(",")
    for (i <- 0 until messageSplitted.size){
      if(messageSplitted(i)!="0"){
        messageFormatter(i)+= messageSplitted(i)
      }
      else {
        messageFormatter(i) = ""
      }
    }
    val email = Email(
      s"Your tickets $booker",
      "scala.qacinemas@gmail.com",
      Seq(to),
      // adds attachment

      // sends text, HTML or both...
      bodyText = Some("who is who"),
      bodyHtml = Some(
        s"""<html><body>
           |<h1>Your Booking $booker:</h1>
           | ${messageFormatter.foldLeft("")((end,line) => if(line != ",") end.concat(line) else end.concat(""))} </p></body></html>""".stripMargin)
    )
    mailerClient.send(email)
  }

}