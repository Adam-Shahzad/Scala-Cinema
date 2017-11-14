package models
import play.api.data._
import play.api.data.Forms._

import scala.collection.mutable.ArrayBuffer

case class SeatSelection(seat: List[Boolean]) {



}
object  SeatSelection {
  val seatList = ArrayBuffer[SeatSelection]()
  val createForm = Form (
    mapping (
      "seat" -> list(boolean)
    )(SeatSelection.apply)(SeatSelection.unapply)
  )
}
