package controllers

import javax.inject.Inject

import models.{Search,UserForm, Users,RegForm}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Cookie, DiscardingCookie}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection.JSONCollection
import controllers.SearchController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import reactivemongo.play.json._
import models.JsonFormats._

import scala.collection.mutable.ArrayBuffer
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

  def loginPage()=Action{implicit request=>
    Ok(views.html.logIn(UserForm.userForm,RegForm.regForm, ""))
  }

  def processLoginForm=Action{ implicit request =>
    val formResult = UserForm.userForm.bindFromRequest()
    formResult.fold({ errors =>
      BadRequest(views.html.logIn(errors,RegForm.regForm,"error with login"))
    },{ form =>
      val logging = Await.result(checkUsernameExist(form.userName,form.hashedPass),5 second)
      if (logging.isEmpty)Ok(views.html.logIn(UserForm.userForm,RegForm.regForm,"incorrect fields"))
      else {
        //Ok(views.html.homepage(Search.createForm)).withSession("user" -> logging.head._id.toString)
        Redirect(routes.SearchController.search()).withSession("user" -> logging.head._id.toString)
      }
    })
  }


  def createUser(fName:String,lName:String, userName:String,email:String, hashedPass:String): Int ={
    val newUserID = Await.result(generateID,5 second) +1
    val newUser = new Users(newUserID,fName,lName,userName,email,hashedPass)
    usersCollection.flatMap(_.insert(newUser))
    newUserID
  }


  def generateID: Future[Int] ={
    val cursor: Future[Cursor[Users]] = usersCollection.map {
      _.find(Json.obj()).sort(Json.obj("$natural" -> -1)).cursor[Users]
    }
    val sortedUsers: Future[ArrayBuffer[Users]] = cursor.flatMap(_.collect[ArrayBuffer]())
    val latestId = sortedUsers.map { Users =>
      Users.head._id
    }
    latestId
  }

  def processRegisterForm = Action {implicit request =>
    val formResult = RegForm.regForm.bindFromRequest()
    formResult.fold({errors=>
      BadRequest(views.html.logIn(UserForm.userForm,errors,errors.toString))
      }, { form =>
      val newUserID = createUser(form.firstName, form.lastName, form.userName, form.email, form.hashedPass)
      Ok(views.html.homepage(Search.createForm)).withSession("user" -> newUserID.toString)


    })
  }

  def logOut = Action { implicit request =>
    //Ok(views.html.homepage(Search.createForm)).withSession("user" -> "")
    Redirect(routes.SearchController.search()).withNewSession
  }

}
