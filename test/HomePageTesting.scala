import org.scalatest._
import selenium._

class HomePageTesting extends FlatSpec with Matchers with HtmlUnit {

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

  "Clicking on the New Releases button" should "navigate user to the new releases gallery" in {
    go to (host)
    click on id("newReleases")
    pageTitle should be ("New Releases")
  }

  "Clicking on the My Ticket Bookings button" should "navigate user to the ticket booking page" in {
    go to (host)
    click on id("ticketBookings")
    pageTitle should be ("Log In")
  }

  "Clicking on a poster of a movie" should "take you to the individual movie page for it" in {
    go to (host)
    click on id("posterClick")
    pageTitle should be ("Individual Movie")
  }

  "The bottom of the home page" should "have a list of offers at the cinema" in {
    go to (host)
    id("greatOffers").webElement.getText should include ("Great Offers")
  }
}