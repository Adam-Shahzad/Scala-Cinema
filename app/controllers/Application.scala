package controllers

import javax.inject.Inject

import models.{Payment, SeatSelection}
import models.Payment

import models.Movies

import play.api._
import play.api.libs.json
import play.api.libs.json._
import play.api.libs.json.{JsPath, Json}
import play.api.mvc._
import reactivemongo.bson.BSONDocument
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

import scala.concurrent.Future

class Application extends Controller {


class Application  @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {

  var seatList = scala.collection.mutable.Map[Int, Char]()
  val seatLetters = ('A' to 'F').toList
  val rowNumbers = (1 to 10).toList

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def homepage = Action {
    val m = new Movies
    Ok(views.html.homepage())
  }

  def classifications = Action {
    Ok(views.html.classifications())
  }

  def individualMovie = Action {
    Ok(views.html.individualMovie())
  }

  def listingsGallery = Action {
    Ok(views.html.listingsGallery())
  }

  def newReleasesGallery = Action {
    Ok(views.html.newReleasesGallery())
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

  def ticketBooking = Action {
    Ok(views.html.ticketBooking())
  }

  def seatSelection = Action {
    val initialise = SeatSelection(List(true,false))
    SeatSelection.seatList.append(initialise)
    Ok(views.html.seatSelection(SeatSelection.createForm, SeatSelection.seatList, "start"))
  }

  def seatSelectionForm = Action{ implicit request =>
    val retriveSeatsForm = SeatSelection.createForm.bindFromRequest()
   retriveSeatsForm.fold({formWithErrors =>
      BadRequest(views.html.seatSelection(SeatSelection.createForm, SeatSelection.seatList,"bad"))
    },{seats =>
     val toAdd = SeatSelection(seats.seat)
      SeatSelection.seatList.append(toAdd)
      Ok(views.html.seatSelection(SeatSelection.createForm, SeatSelection.seatList, "good"))
    })

  }

}