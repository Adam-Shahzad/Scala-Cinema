package models
import play.api.data._
import play.api.data.Forms._

import scala.collection.mutable.ArrayBuffer

case class SeatSelection(seat1A: Boolean, seat2A: Boolean ,seat3A: Boolean ,seat4A: Boolean ,seat5A: Boolean ,seat6A: Boolean ,seat7A: Boolean ,seat8A: Boolean ,seat9A: Boolean ,seat10A: Boolean ) {



}
object  SeatSelection {
  val createForm = Form (
    mapping (
      "seat1A" -> default(boolean, false),
      "seat2A" -> default(boolean, false),
      "seat3A" -> default(boolean, false),
      "seat4A" -> default(boolean, false),
      "seat5A" -> default(boolean, false),
      "seat6A" -> default(boolean, false),
      "seat7A" -> default(boolean, false),
      "seat8A" -> default(boolean, false),
      "seat9A" -> default(boolean, false),
      "seat10A" -> default(boolean, false)

    )(SeatSelection.apply)(SeatSelection.unapply)
  )
}
