package controllers

import javax.inject.Inject

import models.JsonFormats.{BookingFormat, screeningFormat, ticketFormat}
import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json._
import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import play.api.mvc.{Action, Controller}
import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.mailer.MailerClient
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._




object MyHelpers {
  import views.html.helper.FieldConstructor
  implicit val myFields = FieldConstructor(views.html.myFieldConstructorTemplate.f)
}

class Application  @Inject() (val messagesApi: MessagesApi)(val mailerClient: MailerClient)(val reactiveMongoApi: ReactiveMongoApi) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents{


  def screeningCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("screening"))
  def ticketCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("tickets"))
  def bookingCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("bookings"))
  var seatList = scala.collection.mutable.ArrayBuffer[Boolean]()

  def individualMovie(address:Int,newReleases:Boolean,searchString:String) = Action {
    Ok(views.html.individualMovie(address, newReleases, searchString))
  }

  def test = Action{
    Ok(views.html.test())
  }

  def payment = Action {
      Ok(views.html.payment("Please enter your payment details",Payment.createForm))
  }

  def processPaymentForm = Action { implicit request =>
    val formValidationResult = Payment.createForm.bindFromRequest()
    val action = request.body.asFormUrlEncoded.get("action").head
    val mail = new MailerService(mailerClient)


    if (formValidationResult.hasErrors) {
      if (action == "empty") {

        Ok(views.html.payment("Basket Emptied", Payment.createForm))
      }
      else BadRequest(views.html.payment("Please Enter values Correctly", Payment.createForm ))
    }
    else {
      action match {
        case "pay" =>
          val thisPayment = new Payment(formValidationResult.value.head.name,formValidationResult.value.head.number,formValidationResult.value.head.expiry, formValidationResult.value.head.csv )
          mail.sendEmail(thisPayment.name, request.session.get("guestEmail").getOrElse("none"), s"Your Tickets booked with ${thisPayment.csv}")
          Ok(views.html.payment(s"Thanks ${request.session.get("guestName").getOrElse("bomba")} for you purchase! Your tickets are sent to ${request.session.get("guestEmail").getOrElse("bomba")}",Payment.createForm ))
        case "empty" =>
          Ok(views.html.payment("Basket Emptied", Payment.createForm))
      }
    }
  }



  def ticketSelectionForm(movieTitle: String) = Action {implicit request =>
    def guestUserId: String = {
      val id = scala.util.Random
      "guest"+id.nextInt().toString
    }
    var userID = "none"
    val isGuest = true
    if(isGuest) userID = guestUserId

    Ok(views.html.ticketSelection(userID,TicketBooking.createForm, isGuest)).withSession("user" -> userID)
  }

  def getTicketFormAction(movieTitle: String) = Action { implicit request =>
    val formResult = TicketBooking.createForm.bindFromRequest()
    formResult.fold({errors =>
      BadRequest(views.html.ticketSelection(movieTitle,errors, true))
    }, { form =>


      val latestID = Await.result(getLatestBookingID, 5 second)

      Ok(views.html.payment(latestID.toString, Payment.createForm)).withSession(request.session + ("guestName" -> form.guestName.getOrElse("bomba")) + ("guestEmail" -> form.guestEmail.getOrElse("bomba")))


    })
  }

  def getLatestBookingID: Future[Int] = {
    val cursor: Future[Cursor[Booking]] = bookingCollection.map {
      _.find(Json.obj()).sort(Json.obj("$natural" -> -1)).cursor[Booking]
    }
    val sortedBookings: Future[ArrayBuffer[Booking]] = cursor.flatMap(_.collect[ArrayBuffer]())
    val latestId = sortedBookings.map { bookings =>
      bookings.head._id
    }
    latestId
  }

  def gettingTherePage = Action {
    Ok(views.html.gettingThere(Emails.createForm, "Email"))
  }
  def sendEmail = Action { implicit request =>
    val formResult = Emails.createForm.bindFromRequest()
    val mail = new MailerService(mailerClient)
    formResult.fold({errors =>
      BadRequest(views.html.gettingThere(errors,"Please fill in carefully"))
    }, { form =>
      mail.sendEmail(form.subject,form.email,form.emailBody)
      Ok(views.html.gettingThere(Emails.createForm, s"Email sent!"))
    })
  }


}