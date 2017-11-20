package models
import play.api.data._
import play.api.data.Forms._

case class TicketBooking(guestName: Option[String], guestEmail: Option[String], movieTime: String, adultTicket: Int, childTicket: Int, studentTicket: Int, concessionTicket: Int) {

}

object  TicketBooking {
  val createForm = Form(
    mapping (
      "guestName" -> optional(nonEmptyText),
      "guestEmail" -> optional(email),
      "movieTime" -> text,
      "adultTicket" ->  default(number, 0),
      "childTicket" -> default(number, 0),
      "studentTicket" -> default(number, 0),
      "concessionTicket" -> default(number, 0)

    )(TicketBooking.apply)(TicketBooking.unapply)
  )
}
