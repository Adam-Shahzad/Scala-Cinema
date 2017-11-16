package controllers

import javax.inject.Inject

import models.JsonFormats.{BookingFormat, screeningFormat, ticketFormat}
import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import scala.concurrent.{Await, Future}
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.play.json._
import collection._
import models._
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.concurrent.duration._



class Application  @Inject() (val messagesApi: MessagesApi)(val reactiveMongoApi: ReactiveMongoApi) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents{
  

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
          Ok(views.html.payment(s"Thanks ${formValidationResult.value.head.name} for you purchase! Your tickets are ready to be collected",Payment.createForm ))
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

  def seatSelectionForm(movieTitle: String) = Action{implicit  request =>
    Ok(views.html.seatSelection(movieTitle, SeatSelection.createForm))
  }

  def getSeatFormAction(movieTitle: String) = Action { implicit request =>
    val formResult = SeatSelection.createForm.bindFromRequest()
    formResult.fold({errors =>
      BadRequest(views.html.seatSelection(movieTitle,errors))
    },{ form =>
      Ok(views.html.payment(form.seat1A.toString,Payment.createForm))
    })
  }

  def ticketSelectionForm(movieTitle: String) = Action {implicit request =>
    Ok(views.html.ticketSelection(movieTitle,TicketBooking.createForm))
  }

  def getTicketFormAction(movieTitle: String) = Action { implicit request =>
      val formResult = TicketBooking.createForm.bindFromRequest()
      formResult.fold({errors =>
        BadRequest(views.html.ticketSelection(movieTitle,errors))
      },{ form =>
        if(form.selectSeats){
          Ok(views.html.seatSelection(movieTitle, SeatSelection.createForm))
        }
        else{
          Ok(views.html.payment(movieTitle+form.adultTicket,Payment.createForm))
        }
    })
  }


}