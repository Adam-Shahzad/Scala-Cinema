package controllers

import play.api.mvc.{Action, Controller}

class NavigationSimple  extends Controller{

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def classifications = Action {
    Ok(views.html.classifications())
  }

  def openingTimes = Action {
    Ok(views.html.openingTimes())
  }

  def gettingTherePage = Action {
    Ok(views.html.gettingThere())
  }

  def homepage = Action {
    Ok(views.html.homepage())
  }

  def listingsGallery = Action {
    Ok(views.html.listingsGallery())
  }

  def newReleasesGallery = Action {
    Ok(views.html.newReleasesGallery())
  }

  def screens = Action {
    Ok(views.html.screens())
  }

}
