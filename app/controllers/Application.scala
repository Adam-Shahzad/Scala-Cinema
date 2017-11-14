package controllers

import javax.inject.Inject
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


class Application  @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  
val movie = new Movies
  
 var seatList = ArrayBuffer[String]()
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def homepage = Action {
    val m = new Movies
    Ok(views.html.homepage(movie))
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
    val seatLetters = ('A' to 'F').toList
    val rowNumbers = (1 to 10).toList
    Ok(views.html.seatSelection(seatLetters, rowNumbers, seatList))
  }

}