package controllers

import javax.inject.Inject

import models.JsonFormats.{BookingFormat, screeningFormat, ticketFormat, userFormat}
import play.api.libs.json.{JsPath, Json}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.Cursor
import reactivemongo.play.json._

import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import play.api.mvc.{Action, Controller}
import models._
import play.api.data.Form
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
  def usersCollection : Future[JSONCollection] = database.map(_.collection[JSONCollection]("users"))
  var seatList = scala.collection.mutable.ArrayBuffer[Boolean]()


  def individualMovie(address:Int,newReleases:Boolean,searchString:String) = Action { implicit request=>
    Ok(views.html.individualMovie(address, newReleases, searchString, ScreeningTimes.createForm, screenTimesToOptions(address), "Select Your Screening Time"))
  }
  def guestUserId: String = {
    val id = scala.util.Random
    id.nextInt().toString
  }

  def getButtonSelect(address:String, newReleases:String,searchString:String) = Action { implicit request =>
    val formResult = ScreeningTimes.createForm.bindFromRequest()
    formResult.fold({errors =>
      BadRequest(views.html.individualMovie(address.toInt, newReleases.toBoolean, searchString, errors, screenTimesToOptions(address.toInt), "Please Select a Screening Time"))
    }, {form =>
      val isGuest = request.session.get("user").isEmpty | (request.session.get("user").getOrElse("none") contains "guest")
      if(isGuest){
        Ok(views.html.ticketSelection(Movies.title(address.toInt,Movies.currentMovies),address.toInt ,TicketBooking.createForm, screenTimesToOptions(address.toInt), true, false)).withSession(request.session + ("time" -> form.time))
      } else {
        val userID = request.session.get("user").getOrElse("none")
        val thisUser = Await.result(getUserInfoFromDB(userID.toInt), 5 second)
        val filledForm = TicketBooking.createForm.fill(new TicketBooking(s"${thisUser.firstName} ${thisUser.lastName}", s"${thisUser.email}"))
        Ok(views.html.ticketSelection(Movies.title(address.toInt,Movies.currentMovies),address.toInt,filledForm, screenTimesToOptions(address.toInt),true, false)).withSession(request.session)
      }
    })
  }


  ///////////////////////////2
  // from 1 session."user" will be set according to login or guest
  def ticketSelectionForm(movieID: Int, fromIndividualPage : Boolean, newRelease: Boolean) = Action {implicit request =>
    val isGuest = request.session.get("user").isEmpty | (request.session.get("user").getOrElse("none") contains "guest")
    if(isGuest) {
      val userID = "guest" + guestUserId
      if (newRelease)
        {
          Ok(views.html.ticketSelection(Movies.title(movieID,Movies.newMovies),movieID,TicketBooking.createForm, screenTimesToOptions(movieID), fromIndividualPage, newRelease)).withSession("user" -> userID)
        }
      else{
        Ok(views.html.ticketSelection(Movies.title(movieID,Movies.currentMovies),movieID,TicketBooking.createForm, screenTimesToOptions(movieID), fromIndividualPage, newRelease)).withSession("user" -> userID)
      }


    }

    else {
      val userID = request.session.get("user").getOrElse("none")
      val thisUser = Await.result(getUserInfoFromDB(userID.toInt), 5 second)
      val filledForm = TicketBooking.createForm.fill(new TicketBooking(s"${thisUser.firstName} ${thisUser.lastName}", s"${thisUser.email}"))
      if (newRelease)
        {
          Ok(views.html.ticketSelection(Movies.title(movieID,Movies.newMovies),movieID,filledForm, screenTimesToOptions(movieID),fromIndividualPage, newRelease)).withSession(request.session)
        }
      else
        {
          Ok(views.html.ticketSelection(Movies.title(movieID,Movies.currentMovies),movieID,filledForm, screenTimesToOptions(movieID),fromIndividualPage, newRelease)).withSession(request.session)
        }
    }

  }

  def getTicketFormAction( movieID: Int, fromIndividualPage: Boolean, newRelease: Boolean) = Action { implicit request =>
    val formResult = TicketBooking.createForm.bindFromRequest()
    formResult.fold({errors =>
      BadRequest(views.html.ticketSelection(Movies.title(movieID,Movies.currentMovies),movieID,errors, screenTimesToOptions(movieID), fromIndividualPage, newRelease))
    }, { form =>
      val totalPrice = form.adultTicket.getOrElse(0)*11 + form.childTicket.getOrElse(0)*6 + form.concessionTicket.getOrElse(0)*7
      if(fromIndividualPage){
        if(newRelease){
          Redirect(routes.Application.payment(Movies.title(movieID,Movies.newMovies),totalPrice)).withSession(request.session + ("bookerName" -> form.bookerName) + ("bookerEmail" -> form.bookerEmail)
            + ("adult" -> form.adultTicket.getOrElse(0).toString) + ("child" -> form.childTicket.getOrElse(0).toString) + ("movieID" -> movieID.toString)
            + ("concession" -> form.concessionTicket.getOrElse(0).toString))
        }
        else {
          Redirect(routes.Application.payment(Movies.title(movieID,Movies.currentMovies),totalPrice)).withSession(request.session + ("bookerName" -> form.bookerName) + ("bookerEmail" -> form.bookerEmail)
            + ("adult" -> form.adultTicket.getOrElse(0).toString) + ("child" -> form.childTicket.getOrElse(0).toString) + ("movieID" -> movieID.toString)
            + ("concession" -> form.concessionTicket.getOrElse(0).toString))
        }

      }
      else {
        if(newRelease){
          Redirect(routes.Application.payment(Movies.title(movieID,Movies.newMovies),totalPrice)).withSession(request.session + ("bookerName" -> form.bookerName) + ("bookerEmail" -> form.bookerEmail)
            + ("time" -> form.movieTime.getOrElse("none")) + ("adult" -> form.adultTicket.getOrElse(0).toString) + ("child" -> form.childTicket.getOrElse(0).toString) +  ("movieID" -> movieID.toString)
            + ("concession" -> form.concessionTicket.getOrElse(0).toString))
        }
        else {
          Redirect(routes.Application.payment(Movies.title(movieID,Movies.currentMovies),totalPrice)).withSession(request.session + ("bookerName" -> form.bookerName) + ("bookerEmail" -> form.bookerEmail)
            + ("time" -> form.movieTime.getOrElse("TBD")) + ("adult" -> form.adultTicket.getOrElse(0).toString) + ("child" -> form.childTicket.getOrElse(0).toString) +  ("movieID" -> movieID.toString)
            + ("concession" -> form.concessionTicket.getOrElse(0).toString))
        }
      }
    })
  }

  def getUserInfoFromDB(userID: Int): Future[Users] = {
    val cursor: Future[Cursor[Users]] = usersCollection.map {
      _.find(Json.obj("_id" -> userID)).cursor[Users]
    }
    val user : Future[List[Users]] = cursor.flatMap(_.collect[List]())
    user.map{ usr=>
      usr.head
    }
  }




  ///////////////////////////////////3
  def payment(movieTitle: String, totalPrice: Int) = Action { implicit request =>

    Ok(views.html.payment(s"$movieTitle",Payment.createForm, totalPrice))
  }

  def processPaymentForm(totalPrice: Int) = Action { implicit request =>
    val formValidationResult = Payment.createForm.bindFromRequest()
    val action = request.body.asFormUrlEncoded.get("action").head
    val mail = new MailerService(mailerClient)


    if (formValidationResult.hasErrors) {
      if (action == "empty") {

        Ok(views.html.payment("Basket Emptied", Payment.createForm, 0))
      }
      else BadRequest(views.html.payment("Please Enter values Correctly", Payment.createForm, totalPrice ))
    }
    else {
      action match {
        case "pay" =>
          val thisBooking = (request.session.get("time").getOrElse("none") + "," + request.session.get("adult").getOrElse("none") + ","
          + request.session.get("child").getOrElse("none") + ","  + request.session.get("concession").getOrElse("none"))

          mail.sendBookingConfirmation(formValidationResult.value.head.name, request.session.get("bookerEmail").getOrElse("none"), thisBooking)
          val bookedTickets= Map("adult" -> request.session.get("adult").getOrElse("0").toInt, "child" -> request.session.get("child").getOrElse("0").toInt,
            "concession" -> request.session.get("concession").getOrElse("0").toInt)

          var userID = -1

          if(!(request.session.get("user").getOrElse("-1") contains "guest")){
             userID = request.session.get("user").getOrElse("-1").toInt
          }

          processTickets(userID,request.session.get("movieID").getOrElse("-1"),request.session.get("time").getOrElse("none"),bookedTickets)

          Redirect(routes.Application.bookingConfirmationPage)
        case "empty" =>
          Ok(views.html.payment("Basket Emptied", Payment.createForm, 0))
      }
    }
  }
  def bookingConfirmationPage= Action {implicit request=>
    Ok(views.html.bookingConfirmation(s"Thanks ${request.session.get("bookerName").getOrElse("none")} for you purchase! Your tickets have been sent to ${request.session.get("bookerEmail").getOrElse("none")}" ))
  }

  ///////////////////////////////////////4   Adapt to  overall database design
  def insertTicketsToDB(latestBookingID: Int, ticketType: String) = {
    val newTicket = Tickets(bookingID = latestBookingID,ticketType = ticketType)
    ticketCollection.flatMap(_.insert(newTicket))
  }

  def insertBookingToDB(userID: Int,movieID: Int,movieTime: String) = {
    val newBookingID = Await.result(getLatestBookingID(), 5 second) +1
    if(movieTime == "none"){
      val thisScreeningID = -1
      val newBooking = Booking(newBookingID,userID,thisScreeningID)
      bookingCollection.flatMap(_.insert(newBooking))
    }
    else {
      val thisScreeningID = getScreeningID(movieID,movieTime)
      val newBooking = Booking(newBookingID,userID,thisScreeningID)
      bookingCollection.flatMap(_.insert(newBooking))
    }
    Thread.sleep(500)

  }
  def getScreeningID(movieID: Int,movieTime: String): Int= {
    val screenings = Await.result(getScreeningsForMovie(movieID),5 second)
    val thisScreeningID = screenings.filter(_.time == movieTime).head._id
    thisScreeningID
  }

  def processTickets(userID:Int,movieID: String, movieTime: String, tickets: Map[String,Int]) = {
    insertBookingToDB(userID.toInt, movieID.toInt, movieTime )
    for (ticketType <- tickets.keys){
      if(tickets(ticketType) != 0){
        val latestID = Await.result(getLatestBookingID(userID), 5 second)
        for(i <-0 until tickets(ticketType)){
          println(s"\n latest id : $latestID")
          insertTicketsToDB(latestID,ticketType)
        }
      }
    }
  }

  def getLatestBookingID(userID: Int = -1): Future[Int] = {

    val cursor: Future[Cursor[Booking]] = bookingCollection.map {
      if(userID == -1){
          println("\n\n\n user none")
        _.find(Json.obj()).sort(Json.obj("$natural" -> -1)).cursor[Booking]
      }
      else {
        println(s"\n\n\n user $userID")
        _.find(Json.obj("userID" -> userID.toInt)).sort(Json.obj("$natural" -> -1)).cursor[Booking]
      }
    }
    val sortedBookings: Future[ArrayBuffer[Booking]] = cursor.flatMap(_.collect[ArrayBuffer]())
    val latestId = sortedBookings.map { bookings =>
      bookings.head._id
    }
    latestId
  }
  //////////////////////////////////////////////////////////////////////////////////////////

  def gettingTherePage = Action {implicit request=>
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


  def getScreeningsForMovie(movieID: Int) : Future[List[Screening]] ={

    val cursor: Future[Cursor[Screening]] = screeningCollection.map {
      _.find(Json.obj("movie_ID" -> movieID)).cursor[Screening]
    }
    val screenigs : Future[List[Screening]] = cursor.flatMap(_.collect[List]())
    screenigs
  }

  def screenTimesToOptions(movieID: Int): scala.collection.mutable.MutableList[(String,String)] =  {
    val screeningsList = Await.result(getScreeningsForMovie(movieID), 5 second)
    var times = ArrayBuffer[String]()
    screeningsList.foldLeft(times)((times,time) => times += time.time)
    var timeOptions = scala.collection.mutable.MutableList[(String,String)]()
    times.foldLeft(timeOptions)((timeOptions,screenTime) => timeOptions += (screenTime -> screenTime))
    timeOptions
  }
}