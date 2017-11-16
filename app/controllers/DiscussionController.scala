package controllers

import javax.inject.Inject
import models.Discussion
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection.JSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import reactivemongo.play.json._
import models.JsonFormats.discussionFormat

class DiscussionController @Inject() (val messagesApi: MessagesApi)(val reactiveMongoApi: ReactiveMongoApi) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents{

  val mySuggestions: scala.collection.mutable.Set[Discussion] = scala.collection.mutable.Set.empty[Discussion]
  def discussionCollection :Future[JSONCollection] = database.map(_.collection[JSONCollection]("discussion"))

  def discussion = Action {implicit request =>
    val formValidationResult = Discussion.createForm.bindFromRequest
    formValidationResult.fold({ formWithErrors => BadRequest(views.html.discussion(mySuggestions, formWithErrors)) },
      { input =>
        if (!mySuggestions.exists(value => value.desc == input.desc)) {
          val disc = Discussion(input.name, input.email, input.desc, input.filmName, "%1.1f".format(input.rating).toDouble)
          val futureResult = discussionCollection.flatMap(_.insert(disc))
          futureResult.map(_ => Ok("Success"))
          mySuggestions += disc
        }
        Ok(views.html.discussion(mySuggestions, Discussion.createForm))
      })}

  def getDiscussions = Action.async {

    val cursor: Future[Cursor[Discussion]] = discussionCollection.map {
      _.find(Json.obj()).sort(
        Json.obj("created" -> -1)).cursor[Discussion]
    }
    val futureUsersList: Future[List[Discussion]] = cursor.flatMap(_.collect[List]())
    futureUsersList.map { suggestions =>
      suggestions.foreach(mySuggestions += _)
      Ok(views.html.discussion(mySuggestions,Discussion.createForm))
    }
  }
}
