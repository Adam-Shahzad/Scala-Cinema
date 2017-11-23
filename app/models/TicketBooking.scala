package models
import play.api.data._
import play.api.data.Forms._

case class TicketBooking(bookerName: String, bookerEmail: String, movieTime: Option[String] = None, adultTicket: Option[Int] = None, childTicket: Option[Int] = None, concessionTicket: Option[Int]= None) {

}

object  TicketBooking {
  val createForm = Form(
    mapping (
      "bookerName" -> nonEmptyText,
      "bookerEmail" -> email,
      "movieTime" -> optional(text),
      "adultTicket" ->  optional(default(number, 0)),
      "childTicket" -> optional(default(number, 0)),
      "concessionTicket" -> optional(default(number, 0))

    )(TicketBooking.apply)(TicketBooking.unapply)
  )
}
