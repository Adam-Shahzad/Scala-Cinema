package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

case class Discussion (name: String, email:String, desc:String, filmName:String, rating:Double)

object Discussion{

  val createForm = Form(
    mapping (
      "name" -> nonEmptyText,
      "email" -> email,
      "desc" -> nonEmptyText(minLength = 50, maxLength = 500),
      "filmName" -> nonEmptyText,
      "rating" -> of[Double].verifying(value => value > 0.0 && value <= 5.0)
    )(Discussion.apply)(Discussion.unapply)
  )
}