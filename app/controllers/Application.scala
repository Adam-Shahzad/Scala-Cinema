package controllers

import javax.inject.Inject

import models.JsonFormats.{BookingFormat, screeningFormat, ticketFormat}
import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json._

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import play.api.mvc.{Action, Controller}
import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.mailer.MailerClient
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._




object MyHelpers {
  import views.html.helper.FieldConstructor
  implicit val myFields = FieldConstructor(views.html.myFieldConstructorTemplate.f)
}

class Application  @Inject() (val messagesApi: MessagesApi)(val mailerClient: MailerClient)(val reactiveMongoApi: ReactiveMongoApi) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents{


  def screeningCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("screening"))
  def ticketCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("tickets"))
  def bookingCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("bookings"))
  var seatList = scala.collection.mutable.ArrayBuffer[Boolean]()


  def individualMovie(address:Int,newReleases:Boolean,searchString:String) = Action {
    Ok(views.html.individualMovie(address, newReleases, searchString, ScreeningTimes.createForm, screenTimesToOptions(1), "Select Your Screening Time"))
  }

  def getButtonSelect(address:String, newReleases:String,searchString:String) = Action { implicit request =>
    val formResult = ScreeningTimes.createForm.bindFromRequest()
    formResult.fold({errors =>
      BadRequest(views.html.individualMovie(address.toInt, newReleases.toBoolean, searchString, errors, screenTimesToOptions(1), "error"))
    }, {form =>
      Ok(views.html.payment(form.time, Payment.createForm))
    })
  }

///////////////////////////2
// from 1 session."user" will be set according to login or guest
  def ticketSelectionForm(movieID: String) = Action {implicit request =>
    def guestUserId: String = {
      val id = scala.util.Random
      id.nextInt().toString
    }
    var userID = "none"
    val isGuest = false   /// make automatic according to log in
    if(isGuest) userID = "guest"+guestUserId

    val filledForm = TicketBooking.createForm.fill(new TicketBooking("Fahri", "fahriulucaycy@gmail.com"))
    Ok(views.html.ticketSelection(userID,filledForm, isGuest, screenTimesToOptions(1))).withSession("user" -> userID)
  }

  def getTicketFormAction(movieTitle: String) = Action { implicit request =>
    val formResult = TicketBooking.createForm.bindFromRequest()
    formResult.fold({errors =>
      BadRequest(views.html.ticketSelection(movieTitle,errors, true, screenTimesToOptions(1)))
    }, { form =>
      Ok(views.html.payment("noluyo", Payment.createForm)).withSession(request.session + ("bookerName" -> form.bookerName) + ("bookerEmail" -> form.bookerEmail) + ("time" -> form.movieTime.getOrElse("none"))
        + ("adult" -> form.adultTicket.getOrElse(0).toString) + ("child" -> form.childTicket.getOrElse(0).toString) + ("student" -> form.studentTicket.getOrElse(0).toString)
        + ("concession" -> form.concessionTicket.getOrElse(0).toString))
    })
  }




  ///////////////////////////////////3
  def payment = Action { implicit request =>

    Ok(views.html.payment("Payment for",Payment.createForm))
  }

  def processPaymentForm = Action { implicit request =>
    val formValidationResult = Payment.createForm.bindFromRequest()
    val action = request.body.asFormUrlEncoded.get("action").head
    val mail = new MailerService(mailerClient)


    if (formValidationResult.hasErrors) {
      if (action == "empty") {

        Ok(views.html.payment("Basket Emptied", Payment.createForm))
      }
      else BadRequest(views.html.payment("Please Enter values Correctly", Payment.createForm ))
    }
    else {
      action match {
        case "pay" =>
          val thisBooking = (request.session.get("time").getOrElse("none") + "," + request.session.get("adult").getOrElse("none") + ","
          + request.session.get("child").getOrElse("none") + "," + request.session.get("student").getOrElse("none") + "," + request.session.get("concession").getOrElse("none"))

          mail.sendBookingConfirmation(formValidationResult.value.head.name, request.session.get("bookerEmail").getOrElse("what"), thisBooking)
          if(!(request.session.get("user").getOrElse("none") contains "guest")) {
            val bookedTickets= Map("adult" -> request.session.get("adult").getOrElse("0").toInt, "child" -> request.session.get("child").getOrElse("0").toInt, "student" -> request.session.get("student").getOrElse("0").toInt,
              "concession" -> request.session.get("concession").getOrElse("0").toInt)
            processTickets(bookedTickets)
          }

          Ok(views.html.payment(s"Thanks ${request.session.get("bookerName").getOrElse("none")} for you purchase! Your tickets are sent to ${request.session.get("bookerEmail").getOrElse("none")}",Payment.createForm ))
        case "empty" =>
          Ok(views.html.payment("Basket Emptied", Payment.createForm))
      }
    }
  }

  ///////////////////////////////////////4   Adapt to  overall database design
  def insertBookingToDB(latestBookingID: Int, ticketType: String) = {
    val newBooking = Tickets(bookingID = latestBookingID +1,ticketType = ticketType)
    ticketCollection.flatMap(_.insert(newBooking))
  }
  def processTickets(tickets: Map[String,Int]) = {
    for (ticketType <- tickets.keys){
      if(tickets(ticketType) != 0){
        val latestID = Await.result(getLatestBookingID, 5 second)
        for(i <-0 until tickets(ticketType)){
          insertBookingToDB(latestID,ticketType)
        }
      }
    }
  }

  def getLatestBookingID: Future[Int] = {
    val cursor: Future[Cursor[Booking]] = bookingCollection.map {
      _.find(Json.obj()).sort(Json.obj("$natural" -> -1)).cursor[Booking]
    }
    val sortedBookings: Future[ArrayBuffer[Booking]] = cursor.flatMap(_.collect[ArrayBuffer]())
    val latestId = sortedBookings.map { bookings =>
      bookings.head._id
    }
    latestId
  }
  //////////////////////////////////////////////////////////////////////////////////////////

  def gettingTherePage = Action {
    Ok(views.html.gettingThere(Emails.createForm, "Email"))
  }
  def sendEmail = Action { implicit request =>
    val formResult = Emails.createForm.bindFromRequest()
    val mail = new MailerService(mailerClient)
    formResult.fold({errors =>
      BadRequest(views.html.gettingThere(errors,"Please fill in carefully"))
    }, { form =>
      mail.sendEmail(form.subject,form.email,form.emailBody)
      Ok(views.html.gettingThere(Emails.createForm, s"Email sent!"))


    })
  }


  def getScreenTimesForMovie(movieID: Int) : Future[List[Screening]] ={

    val cursor: Future[Cursor[Screening]] = screeningCollection.map {
      _.find(Json.obj("movie_ID" -> movieID)).cursor[Screening]
    }
    val screenTimes : Future[List[Screening]] = cursor.flatMap(_.collect[List]())
    screenTimes
  }

  def screenTimesToOptions(movieID: Int): scala.collection.mutable.MutableList[(String,String)] =  {
    val screeningsList = Await.result(getScreenTimesForMovie(movieID), 5 second)
    var times = ArrayBuffer[String]()
    screeningsList.foldLeft(times)((times,time) => times += time.time)
    var timeOptions = scala.collection.mutable.MutableList[(String,String)]()
    times.foldLeft(timeOptions)((timeOptions,screenTime) => timeOptions += (screenTime -> screenTime))
    timeOptions
  }
}