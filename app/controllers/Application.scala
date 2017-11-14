package controllers

import javax.inject.Inject
import models.JsonFormats.BookingFormat
import models.{Booking, Movies, Payment}

import play.api._
import play.api.libs.json
import play.api.libs.json._
import play.api.libs.json.{JsPath, Json}
import play.api.mvc._
import reactivemongo.bson.BSONDocument
import play.api.i18n.{I18nSupport, MessagesApi}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.play.json._
import collection._
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._


class Application  @Inject() (val messagesApi: MessagesApi)(val reactiveMongoApi: ReactiveMongoApi) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents{
  

  def bookingCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("bookings"))

  var seatList = ArrayBuffer[String]()



  val newMovies = new Movies(0)
  val currentMovies = new Movies(1)

  //var seatList = ArrayBuffer[String]

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def homepage = Action {
    Ok(views.html.homepage(newMovies))

  }

  def classifications = Action {
    Ok(views.html.classifications())
  }

  def individualMovie = Action {
    Ok(views.html.individualMovie())
  }

  def listingsGallery = Action {
    Ok(views.html.listingsGallery(currentMovies))
  }

  def newReleasesGallery = Action {
    Ok(views.html.newReleasesGallery(newMovies))
  }

  def openingTimes = Action {
    Ok(views.html.openingTimes())
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
        case "pay" => Ok(views.html.payment("Thanks for you purchase! Your tickets are ready to be collected",Payment.createForm ))
        case "empty" =>

          Ok(views.html.payment("Basket Emptied", Payment.createForm))
      }
    }
  }

  def screens = Action {
    Ok(views.html.screens())
  }

  def getBooking:Future[List[Booking]]  = {
    val cursor: Future[Cursor[Booking]] = bookingCollection.map{
      _.find(Json.obj()).cursor[Booking]
    }

    val futureBooking : Future[List[Booking]] = cursor.flatMap(_.collect[List]())

    futureBooking

  }

  def loadBookingPage = Action {
    val result = Await.result(getBooking, 5 second)
    Ok(views.html.ticketBooking(result.head))
  }

//  def seatSelection = Action {
//    val seatLetters = ('A' to 'F').toList
//    val rowNumbers = (1 to 10).toList
//    Ok(views.html.seatSelection(seatLetters, rowNumbers, seatList))
//  }

}