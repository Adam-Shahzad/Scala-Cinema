package controllers
import javax.inject.Inject

import models.Emails
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.mailer.MailerClient
import models.UserForm
import play.api.mvc.{Action, Controller}

class NavigationSimple  @Inject() (val messagesApi: MessagesApi)(val mailerClient: MailerClient)extends Controller with I18nSupport{

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
    Ok(views.html.gettingThere(Emails.createForm, "Email"))
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

  def aroundUs = Action{
    Ok(views.html.aroundUs())
  }

  def aboutUs = Action {
    Ok(views.html.AboutUs())
  }
}
