package models
import play.api.data._
import play.api.data.Forms._

case class TicketBooking(bookerName: String, movieTime: String, adultTicket: Int, childTicket: Int, studentTicket: Int, concessionTicket: Int, selectSeats: Boolean) {

}

object  TicketBooking {
  val createForm = Form(
    mapping (
      "bookerName" -> nonEmptyText,
      "movieTime" -> text,
      "adultTicket" ->  default(number, 0),
      "childTicket" -> default(number, 0),
      "studentTicket" -> default(number, 0),
      "concessionTicket" -> default(number, 0),
      "selectSeats" -> boolean
    )(TicketBooking.apply)(TicketBooking.unapply)
  )
}
