package models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._

case class Search(value:String)

object Search{

  implicit val createForm = Form {
    mapping {
      "value" -> nonEmptyText
    }(Search.apply)(Search.unapply)
  }
}