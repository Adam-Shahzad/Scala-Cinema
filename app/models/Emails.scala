package models

import play.api.data._
import play.api.data.Forms._

case class Emails (email:String, subject:String, emailBody:String)

object Emails {

  val createForm = Form{
    mapping (
      "email" -> email,
      "subject" -> nonEmptyText,
      "emailBody" -> nonEmptyText(minLength=50, maxLength=1000)
    )(Emails.apply)(Emails.unapply)
  }

}
