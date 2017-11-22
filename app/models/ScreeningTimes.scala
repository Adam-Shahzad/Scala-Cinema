package models

import play.api.data._
import play.api.data.Forms._

case class ScreeningTimes( time: String) {


}

object  ScreeningTimes {
  val createForm = Form(
    mapping (
      "time" -> text
    )(ScreeningTimes.apply)(ScreeningTimes.unapply)
  )
}

