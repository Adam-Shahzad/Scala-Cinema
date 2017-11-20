package models

import play.api.data._
import play.api.data.Forms._

case class UserForm(userName: String, hashedPass: String)

object UserForm{
  val userForm = Form(
    mapping(
      "userName" -> nonEmptyText,
      "hashedPass" -> text
    )(UserForm.apply)(UserForm.unapply)
  )
}
