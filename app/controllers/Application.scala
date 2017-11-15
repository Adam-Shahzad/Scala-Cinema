package controllers

import javax.inject.Inject


import models.JsonFormats.{BookingFormat, discussionFormat,ticketFormat,screeningFormat}
import models.{Booking, Movies, Payment,Tickets,Screening}


import models.JsonFormats.{BookingFormat, discussionFormat}
import models._
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

  def screeningCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("screening"))
  def ticketCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("tickets"))
  def bookingCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("bookings"))
  def discussionCollection :Future[JSONCollection] = database.map(_.collection[JSONCollection]("discussion"))
  val mySuggestions: scala.collection.mutable.Set[Discussion] = scala.collection.mutable.Set.empty[Discussion]
  var seatList = ArrayBuffer[String]()
  val newMovies = new Movies(0)
  val currentMovies = new Movies(1)
  val nearBy = new NearMe(0)

  //var seatList = ArrayBuffer[String]

  def aroundUs = Action{
    Ok(views.html.aroundUs(nearBy))
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def homepage = Action {
    Ok(views.html.homepage(newMovies))

  }

  def classifications = Action {
    Ok(views.html.classifications())
  }


  def individualMovie(address:Int) = Action {
    Ok(views.html.individualMovie(currentMovies, address))
  }

  def individualNewMovie(address:Int) = Action {
    Ok(views.html.individualMovie(newMovies, address))
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


  def getDiscussions = Action.async {

    val cursor: Future[Cursor[Discussion]] = discussionCollection.map {
      _.find(Json.obj()).sort(
        Json.obj("created" -> -1)).cursor[Discussion]
    }
    val futureUsersList: Future[List[Discussion]] = cursor.flatMap(_.collect[List]())
    futureUsersList.map { suggestions =>
      suggestions.foreach(mySuggestions += _)
      Ok(views.html.discussion(mySuggestions,Discussion.createForm,newMovies))
    }
  }


  def discussion = Action {implicit request =>
      val formValidationResult = Discussion.createForm.bindFromRequest
      formValidationResult.fold({ formWithErrors => BadRequest(views.html.discussion(mySuggestions, formWithErrors,newMovies)) },
    { input =>
      if (!mySuggestions.exists(value => value.desc == input.desc)) {
        val disc = Discussion(input.name, input.email, input.desc, input.filmName, "%1.1f".format(input.rating).toDouble)
        val futureResult = discussionCollection.flatMap(_.insert(disc))
        futureResult.map(_ => Ok("Success"))
        mySuggestions += disc
      }
      Ok(views.html.discussion(mySuggestions, Discussion.createForm,newMovies))
    })}


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

  def screens = Action {
    Ok(views.html.screens())
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

  def gettingTherePage = Action {
    Ok(views.html.gettingThere())
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
    val ticketResult = Await.result(getTicketInfo(bookingResult.head._id),5 second)
    val screeningResult = Await.result(getScreeningInfo(bookingResult.head.screeningID),5 second)

    println(bookingResult.toString())
    println(ticketResult.toString())
    println(screeningResult.toString())

    Ok(views.html.ticketBooking(bookingResult.head,ticketResult,screeningResult.head, currentMovies))
  }



//  def seatSelection = Action {
//    val seatLetters = ('A' to 'F').toList
//    val rowNumbers = (1 to 10).toList
//    Ok(views.html.seatSelection(seatLetters, rowNumbers, seatList))
//  }

}