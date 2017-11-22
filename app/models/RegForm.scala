package models

import play.api.data._
import play.api.data.Forms._

case class RegForm (
                 firstName:String,
                 lastName:String,
                 userName:String,
                 email:String,
                 hashedPass:String
                 )

object RegForm{

  val regForm = Form(
    mapping(
      "firstName" -> text,
      "lastName" -> text,
      "userName" -> nonEmptyText,
      "email" -> nonEmptyText,
      "hashedPass" -> nonEmptyText
    )(RegForm.apply)(RegForm.unapply)

  )
}