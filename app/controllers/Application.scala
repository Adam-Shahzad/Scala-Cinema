package controllers

import models.Movies
import play.api._
import play.api.libs.json
import play.api.libs.json._
import play.api.libs.json.{JsPath, Json}
import play.api.mvc._

import scala.concurrent.Future

class Application extends Controller {

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
    Ok(views.html.payment())
  }

  def screens = Action {
    Ok(views.html.screens())
  }

  def ticketBooking = Action {
    Ok(views.html.ticketBooking())
  }

}