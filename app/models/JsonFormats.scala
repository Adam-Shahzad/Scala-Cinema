package models
import play.api.libs.json.Json

object JsonFormats {
  implicit val BookingFormat = Json.format[Booking]
  implicit val discussionFormat = Json.format[Discussion]
  implicit val ticketFormat = Json.format[Tickets]
  implicit val screeningFormat = Json.format[Screening]
  implicit val usersFormat = Json.format[Users]
  implicit val emailFormat = Json.format[Emails]
  implicit val search = Json.format[Search]
}
