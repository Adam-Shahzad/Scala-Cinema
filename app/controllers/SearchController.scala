package controllers

import javax.inject.Inject

import models.{Movies, Search}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

class SearchController @Inject() (val messagesApi: MessagesApi)(val reactiveMongoApi: ReactiveMongoApi) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents{

  def search = Action{implicit request =>
    val formValidationResult = Search.createForm.bindFromRequest
    formValidationResult.fold({formWithErrors => BadRequest(views.html.homepage(formWithErrors))},
      { input => {
        Ok(views.html.searchResult(Movies.filterList(input.value)))
        }
      })}
}
