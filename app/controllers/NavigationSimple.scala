package controllers
import javax.inject.Inject

import models.{Emails, Search, UserForm}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.mailer.MailerClient
import play.api.mvc.{Action, Controller}

class NavigationSimple  @Inject() (val messagesApi: MessagesApi)(val mailerClient: MailerClient)extends Controller with I18nSupport{


  def classifications = Action {implicit request=>
    Ok(views.html.classifications())
  }

  def openingTimes = Action {implicit request=>
    Ok(views.html.openingTimes())
  }

  def gettingTherePage = Action {implicit request=>
    Ok(views.html.gettingThere(Emails.createForm, "Email"))
  }


  def homepage = Action {implicit request=>
    Ok(views.html.homepage(Search.createForm))
  }

  def listingsGallery = Action {implicit request=>
    Ok(views.html.listingsGallery())
  }

  def newReleasesGallery = Action {implicit request=>
    Ok(views.html.newReleasesGallery())
  }

  def screens = Action {implicit request=>
    Ok(views.html.screens())
  }

  def aroundUs = Action{implicit request=>
    Ok(views.html.aroundUs())
  }

  def aboutUs = Action {implicit request=>
    Ok(views.html.AboutUs())
  }
}
