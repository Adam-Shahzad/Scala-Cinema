package controllers

import javax.inject.Inject

import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import play.api.mvc.{Action, Controller}
import models._
import play.api.i18n.{I18nSupport, MessagesApi}






class Application  @Inject() (val messagesApi: MessagesApi)(val reactiveMongoApi: ReactiveMongoApi) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents{
  


  var seatList = scala.collection.mutable.ArrayBuffer[Boolean]()

  def individualMovie(address:Int,newReleases:Boolean) = Action {
    Ok(views.html.individualMovie(address, newReleases))
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
        case "pay" =>
          val thisPayment = new Payment(formValidationResult.value.head.name,formValidationResult.value.head.number,formValidationResult.value.head.expiry, formValidationResult.value.head.csv )
          Ok(views.html.payment(s"Thanks ${formValidationResult.value.head.name} for you purchase! Your tickets are ready to be collected",Payment.createForm ))
        case "empty" =>
          Ok(views.html.payment("Basket Emptied", Payment.createForm))
      }
    }
  }


  def seatSelectionForm(movieTitle: String) = Action{implicit  request =>
    Ok(views.html.seatSelection(movieTitle, SeatSelection.createForm))
  }

  def getSeatFormAction(movieTitle: String) = Action { implicit request =>
    val formResult = SeatSelection.createForm.bindFromRequest()
    formResult.fold({errors =>
      BadRequest(views.html.seatSelection(movieTitle,errors))
    },{ form =>
      Ok(views.html.payment(form.seat1A.toString,Payment.createForm))
    })
  }

  def ticketSelectionForm(movieTitle: String) = Action {implicit request =>
    Ok(views.html.ticketSelection(movieTitle,TicketBooking.createForm))
  }

  def getTicketFormAction(movieTitle: String) = Action { implicit request =>
      val formResult = TicketBooking.createForm.bindFromRequest()
      formResult.fold({errors =>
        BadRequest(views.html.ticketSelection(movieTitle,errors))
      },{ form =>
        if(form.selectSeats){
          Ok(views.html.seatSelection(movieTitle, SeatSelection.createForm))
        }
        else{
          Ok(views.html.payment(movieTitle+form.adultTicket,Payment.createForm))
        }
    })
  }


}