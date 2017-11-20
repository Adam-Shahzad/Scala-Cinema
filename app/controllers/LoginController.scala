package controllers

import javax.inject.Inject

import models.{UserForm, Users}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Cookie, DiscardingCookie}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import reactivemongo.play.json._
import models.JsonFormats._

import scala.concurrent.duration._


class LoginController @Inject() (val messagesApi: MessagesApi)(val reactiveMongoApi: ReactiveMongoApi) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents{

  def usersCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("users"))

  def checkUsernameExist(userName:String, password:String) ={
    val cursor: Future[Cursor[Users]] = usersCollection.map{
      _.find(Json.obj("userName"->userName,"hashedPass"->password)).cursor[Users]
    }

    val futureUser : Future[List[Users]] = cursor.flatMap(_.collect[List]())

    futureUser
  }

  def loginPage()=Action{
    Ok(views.html.logIn(UserForm.userForm,"poop"))
  }

  def processLoginForm=Action{ implicit request =>
    val formResult = UserForm.userForm.bindFromRequest()
    formResult.fold({ errors =>
      BadRequest(views.html.logIn(errors,"error"))
    },{ form =>
      val logging = Await.result(checkUsernameExist(form.userName,form.hashedPass),5 second)
      if (logging.isEmpty)Ok(views.html.logIn(UserForm.userForm,"incorrect fields"))
      else {

        Ok(views.html.homepage()).withCookies(Cookie("userCookie",logging.head._id.toString))

      }
    })
  }




}
