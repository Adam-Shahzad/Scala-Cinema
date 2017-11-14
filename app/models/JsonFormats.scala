package models
import play.api.libs.json.Json

object JsonFormats {
implicit val BookingFormat = Json.format[Booking]
}
