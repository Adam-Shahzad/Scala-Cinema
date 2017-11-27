import javax.inject.Inject

import controllers.NavigationSimple

import scala.concurrent.Future
import org.scalatestplus.play._
import play.api.i18n.MessagesApi
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._


class HomePageTesting @Inject() (val messagesApi: MessagesApi) extends PlaySpec with Results{
  "Example Page#index" should {
    "should be valid" in {
      //val controller = new NavigationSimple(messagesApi)()
      //val result: Future[Result] = controller.homepage().apply(FakeRequest())
      //val bodyText: String = contentAsString(result)
      //bodyText mustBe "ok"
    }
  }

}
