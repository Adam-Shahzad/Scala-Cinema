import controllers.LoginController

import collection.mutable.Stack
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._


class ExampleSpec extends FlatSpec with Matchers  with BeforeAndAfter with org.scalatest.mockito.MockitoSugar{

  "processLoginForm" should "redirect to the homepage and create a session cookie" in {
    val service = mock[LoginController]

    service.processRegisterForm()
  }

  it should "return an error when there are no users with that username or password" in {

  }
}