package models
import org.joda.time.DateTime
import play.api.data._
import play.api.data.Forms._

case class Payment(name: String, number: String, expiry: DateTime, csv: Int) {

}

object Payment {

  val createForm = Form(
    mapping (
      "name" -> nonEmptyText,
      "number" -> nonEmptyText(minLength = 16, maxLength = 16),
      "expiry" -> jodaDate(pattern = "mm/yy"),
      "csv" -> number(min = 100, max = 999)
    )(Payment.apply)(Payment.unapply)
  )
}
