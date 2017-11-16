package controllers

import javax.inject.Inject

import models.{Booking, Screening, Tickets}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import reactivemongo.play.json._
import models.JsonFormats._

import scala.concurrent.duration._

class TicketBookingController @Inject() (val messagesApi: MessagesApi)(val reactiveMongoApi: ReactiveMongoApi) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents{

  def screeningCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("screening"))
  def ticketCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("tickets"))
  def bookingCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("bookings"))

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

}

