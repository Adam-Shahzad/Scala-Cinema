import org.scalatest._
import selenium._

class SearchTesting extends FlatSpec with Matchers with HtmlUnit {

  val host = "http://localhost:9000/"

  "The blog app home page" should "have the correct title" in {
    go to (host)
    pageTitle should be ("Homepage")
  }
}
