package models

import play.api.data._
import play.api.data.Forms._

case class Discussion (name: String, email:String, desc:String, filmName:String, rating:Int)

object Discussion{

  val createForm = Form(
    mapping (
      "name" -> nonEmptyText,
      "email" -> email,
      "desc" -> nonEmptyText(minLength = 50),
      "filmName" -> nonEmptyText,
      "rating" -> number(min = 0, max = 5)
    )(Discussion.apply)(Discussion.unapply)
  )
}