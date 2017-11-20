package controllers

import javax.inject.Inject

import models.JsonFormats.{BookingFormat, screeningFormat, ticketFormat}
import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.play.json._
import collection._
import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.mailer.MailerClient
import reactivemongo.bson.BSONDocument

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._



class Application  @Inject() (val messagesApi: MessagesApi)(val mailerClient: MailerClient)(val reactiveMongoApi: ReactiveMongoApi) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents{
  

  def screeningCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("screening"))
  def ticketCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("tickets"))
  def bookingCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("bookings"))
  var seatList = scala.collection.mutable.ArrayBuffer[Boolean]()

  def individualMovie(address:Int,newReleases:Boolean) = Action {
    Ok(views.html.individualMovie(address, newReleases))
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

  def getBooking(userID:Int):Future[List[Booking]]  = {
    val cursor: Future[Cursor[Booking]] = bookingCollection.map{
      _.find(Json.obj("userID"->userID)).cursor[Booking]
    }

    val futureBooking : Future[List[Booking]] = cursor.flatMap(_.collect[List]())

    futureBooking

  }

  def getTicketInfo(bookingID:Int): Future[List[Tickets]] = {

    val cursor: Future[Cursor[Tickets]] = ticketCollection.map{
      _.find(Json.obj("bookingID"->bookingID)).cursor[Tickets]
    }

    val futureTickets : Future[List[Tickets]] = cursor.flatMap(_.collect[List]())

    futureTickets
  }

  def getScreeningInfo(screeningID:Int): Future[List[Screening]] = {

    val cursor: Future[Cursor[Screening]] = screeningCollection.map{
      _.find(Json.obj("_id"->screeningID)).cursor[Screening]
    }

    val futureScreening: Future[List[Screening]] = cursor.flatMap(_.collect[List]())

    futureScreening
  }

  def loadBookingPage(userID:Int) = Action {
    val bookingResult = Await.result(getBooking(userID), 5 second)
    val ticketResult = bookingResult.map{br => Await.result(getTicketInfo(br._id),5 second)}
    val screeningResult = bookingResult.map{br=> Await.result(getScreeningInfo(br.screeningID),5 second).head}

    Ok(views.html.ticketBooking(bookingResult,ticketResult,screeningResult))
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
  //  def insertBookingToDB(currentUserID : String) = {
  //
  //    val selector = BSONDocument("_id" -> currentUserID)
  //    val newItem = Json.obj(
  //      ""
  //
  //
  //  }

}