
import org.scalatest._
import selenium._

class BlogSpec2 extends FlatSpec with Matchers with HtmlUnit {

  val host = "http://localhost:9000/"

  "The app home page" should "have the correct title" in {
    go to (host)
    pageTitle should be ("Homepage")
  }

  "Clicking on the listings Gallery button" should "navigate user to the listings gallery" in {
    go to (host)
    click on id("listingsGallery")
    pageTitle should be ("Listing Gallery")
  }
}


