package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

case class Discussion (name: String, email:String, desc:String, filmName:String, rating:Double)

object Discussion{

//  val allNumber = """\d*""".r
//  val allLetters = """[A-Za-z]*""".r
//
//  val checkContstraint: Constraint[String] = Constraint("constraints.passwordcheck")({
//    plainText =>
//      val errors = plainText match {
//        case allNumber() => Seq(ValidationError("Password is all numbers"))
//        case allLetters() => Seq(ValidationError("Password is all characters"))
//        case _ => Nil
//      }
//      if (errors.isEmpty)
//        Valid
//      else
//          Invalid(errors)
//  })



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